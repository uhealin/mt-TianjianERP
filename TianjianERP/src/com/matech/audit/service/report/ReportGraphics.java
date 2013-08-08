package com.matech.audit.service.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.matech.audit.service.report.model.ProcProperties;
import com.matech.audit.service.report.model.StepProperties;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;

public class ReportGraphics {

	private int NODE_WIDTH = 100;	//节点宽
	private int NODE_HEIGHT = 40;	//节点高
	private int X = 120;	//X轴递增
	private int Y = 60;		//Y轴递增
	private String filePath = null;
	private String systemId = null;

	private Connection conn = null;

	public ReportGraphics(Connection conn, String systemId) {
		this.conn = conn;
		this.systemId = systemId;

        try {
            this.filePath = org.del.DelPublic.getWarPath();
        } catch (Exception e) {
            this.filePath = org.del.DelPublic.getClassRoot() + "../../";
        }
        if (this.filePath.substring(0, 1).equals("/")) {
            this.filePath = this.filePath.substring(1);
        }
        if (!this.filePath.substring(this.filePath.length() - 1, this.filePath.length()).equals("/")) {
            this.filePath += "/";
        }

        this.filePath += "ReportProject/graphics/data/";

		File file = new File(this.filePath);
		if(!file.exists()) {
			file.mkdirs();
		}
	}

	/**
	 * 获得节点坐标
	 * @param procList
	 * @param customerId
	 * @return
	 * @throws Exception
	 */
	private int[] getXY(List procList,String customerId) throws Exception {

		int[] xy = {-1,-1};
		for(int i=0; i < procList.size(); i++) {
			ProcProperties procProperties = (ProcProperties)procList.get(i);

			if(customerId.equals(procProperties.getId())) {
				xy[0] = procProperties.getX();// - (X / 2) - 2;
				xy[1] = procProperties.getY();// - (Y / 2) - 8;
			}
		}

		return xy;
	}

	/**
	 * 生成合并报表关系图
	 * @param systemId
	 * @return
	 * @throws Exception
	 */
	public String generateNet() throws Exception {
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;

		ResultSet rs = null;
		ResultSet rs2 = null;
		StepProperties stepProperties = null;
		String xmlString = null;

		List procList = new ArrayList();
		List stepList = new ArrayList();

		int x = 50;
		int y = 50;

		generateNet(procList, "0", x , y);

		try {
			String sql = " select xml from asdb.k_customergraphics where systemId=? ";
			ps2 = conn.prepareStatement(sql);
			ps2.setString(1, systemId);
			rs2 = ps2.executeQuery();

			if(rs2.next()) {
				xmlString = rs2.getString(1);
			} else {
//				sql = " select conCustomerid,beConedCustomerid,controlRate "
//					+ " from k_customercontrol a,k_customerrelation b "
//					+ " where systemId=? "
//					+ " order by conCustomerid ";
				sql = " select b.autoId,c.autoId,a.controlRate,a.conCustomerid,a.beConedCustomerid "
					+ " from k_customercontrol a "
					+ " left join k_customerrelation b on a.systemId = ? and a.conCustomerid = b.customerId "
					+ " left join k_customerrelation c on a.systemId = ? and a.beConedCustomerid = c.customerId "
					+ " where a.systemId=? "
					+ " order by a.conCustomerid ";

				ps = conn.prepareStatement(sql);
				ps.setString(1, systemId);
				ps.setString(2, systemId);
				ps.setString(3, systemId);
				rs = ps.executeQuery();

				int i=0;
				while(rs.next()) {
					String fromCustomerId = rs.getString(1);
					String toCustomerId = rs.getString(2);

					//控股公司节点坐标
					int[] fromXY = getXY(procList,fromCustomerId);

					//被控股节点坐标
					int[] toXY = getXY(procList,toCustomerId);

					if(fromXY[0] != -1 && fromXY[1] != -1
							&& toXY[0] != -1 && toXY[1] != -1) {

						int fromX = fromXY[0] + (this.NODE_WIDTH / 2);
						int fromY = fromXY[1] + this.NODE_HEIGHT;
						int toX = toXY[0];// + this.NODE_WIDTH;
						int toY = toXY[1] + (this.NODE_HEIGHT / 2);

						stepProperties = new StepProperties();
						stepProperties.setId(String.valueOf(i));
						stepProperties.setText(rs.getString(3) + "%");
						stepProperties.setFrom(fromCustomerId);
						stepProperties.setTo(toCustomerId);
						stepProperties.setCond(String.valueOf(i) + "=\"" + String.valueOf(i) +"\"");

						String points = fromX + "," + fromY + ","
									  + fromX + "," + toY + ","
									  + toX + "," + toY;

						String inset = (toX-fromX)/2/4*3 +"pt," + (toY-fromY)/2/4*3 +"pt";

						stepProperties.setPoints(points);	// x,y,x1,y1,x2,y2
						stepProperties.setFromRelX("0");
						stepProperties.setFromRelY("0");
						stepProperties.setToRelX("0");
						stepProperties.setToRelY("0");
						stepProperties.setShapetype("PolyLine");
						stepProperties.setStartArrow("none");
						stepProperties.setEndArrow("Classic");
						stepProperties.setStrokeWeight("1");
						stepProperties.setZIndex("1");
						stepProperties.setInset(inset);
						stepList.add(stepProperties);
						i++;
					}
				}

				xmlString =  generateXmlFile(procList,stepList);

				sql = " insert into asdb.k_customergraphics(systemId,xml) "
					+ " values(?,?) ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, systemId);
				ps.setString(2, xmlString);
				ps.executeUpdate();
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(rs2);
			DbUtil.close(ps2);
		}

		return xmlStringToFile(xmlString);
	}

	/**
	 * 显示网状列出图
	 * @param systemId
	 * @param procList
	 * @param parentId
	 * @param x
	 * @param y
	 * @throws Exception
	 */
	private void generateNet(List procList, String parentId,int x, int y) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String customerId = null;
		String propery = null;
		String autoId = null;
		ProcProperties procProperties = null;

		try {
			String sql = " select a.customerId,a.property,a.parentid,a.nodeName,b.departName,a.autoId "
					   + " from asdb.k_customerrelation a "
					   + " left join asdb.k_customer b "
					   + " on a.customerId=b.departId "
					   + " where systemId=? "
					   + " and parentId=? "
					   + " order by level0,property ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, systemId);
			ps.setString(2, parentId);
			rs = ps.executeQuery();

			while(rs.next()) {
				customerId = rs.getString(1);
				propery = rs.getString(2);
				autoId = rs.getString(6);

				if("2000000000".equals(propery)) {
					//合并接点
					procProperties = new ProcProperties();
					procProperties.setId(autoId);
					procProperties.setText("<strong>" + rs.getString(4) + "</strong>");
					procProperties.setProcType("BeginProc");
					procProperties.setActFlag("1010");
					procProperties.setWaittime("");
					procProperties.setIsSltTrans("1");
					procProperties.setIsSameCredit("0");
					procProperties.setShapetype("Rect");
					procProperties.setWidth(this.NODE_WIDTH);
					procProperties.setHeight(this.NODE_HEIGHT);
					procProperties.setX(x);
					procProperties.setY(y);
					procProperties.setTextWeight("9pt");
					procProperties.setStrokeWeight("1");
					procProperties.setZIndex("1");
					procList.add(procProperties);

					x += this.X - 80;
					y += this.Y;
					generateNet(procList, autoId,x,y);
				} else if("0000000000".equals(propery)) {
					//母公司
					procProperties = new ProcProperties();
					procProperties.setId(autoId);
					procProperties.setText("<strong>" + rs.getString(5) + "(" + customerId + ")" + "</strong>");
					procProperties.setProcType("NormalProc");
					procProperties.setActFlag("1010");
					procProperties.setWaittime("");
					procProperties.setIsSltTrans("1");
					procProperties.setIsSameCredit("0");
					procProperties.setShapetype("Rect");
					procProperties.setWidth(this.NODE_WIDTH);
					procProperties.setHeight(this.NODE_HEIGHT);
					procProperties.setX(x);
					procProperties.setY(y);
					procProperties.setTextWeight("9pt");
					procProperties.setStrokeWeight("1");
					procProperties.setZIndex("1");
					procList.add(procProperties);

					x += this.X;
					y += this.Y;
					//generateNet(systemId, procList, rs.getString(6),x,y);

				} else {
					//子公司
					procProperties = new ProcProperties();
					procProperties.setId(autoId);
					procProperties.setText(rs.getString(5) + "(" + customerId + ")");
					procProperties.setProcType("NormalProc");
					procProperties.setActFlag("1010");
					procProperties.setWaittime("");
					procProperties.setIsSltTrans("1");
					procProperties.setIsSameCredit("0");
					procProperties.setShapetype("RoundRect");
					procProperties.setWidth(this.NODE_WIDTH);
					procProperties.setHeight(this.NODE_HEIGHT);
					procProperties.setX(x);
					procProperties.setY(y);
					procProperties.setTextWeight("9pt");
					procProperties.setStrokeWeight("1");
					procProperties.setZIndex("1");
					procList.add(procProperties);

					y += this.Y;
				}
			}

			try {
				DbUtil.close(rs);
				DbUtil.close(ps);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 生成XML文件
	 * @param procList
	 * @param stepList
	 * @throws Exception
	 */
	private String generateXmlFile(List procList, List stepList) throws Exception {
		Element topFlow;
		Element procs;
		Element proc;
		Element baseProperties;
		Element vmlProperties;
		Element steps;
		Element step;

		Document Doc;
		topFlow = new Element("TopFlow");
		Doc = new Document(topFlow);
		topFlow = Doc.getRootElement();

		String randomId = DELUnid.getNumUnid();
		topFlow.setAttribute("id", randomId);
		topFlow.setAttribute("formid", randomId);
		topFlow.setAttribute("filename", randomId + ".xml");
		topFlow.setAttribute("formid", "");
		topFlow.setAttribute("text", "关系图");
		topFlow.setAttribute("password", "");

		// 生成proc
		procs = new Element("Procs");
		topFlow.addContent(procs);

		for (int i = 0; i < procList.size(); i++) {
			proc = new Element("Proc");
			ProcProperties procProperties = (ProcProperties) procList.get(i);
			baseProperties = new Element("BaseProperties");

			baseProperties.setAttribute("id", procProperties.getId());
			baseProperties.setAttribute("text", procProperties.getText());
			baseProperties.setAttribute("procType", procProperties.getProcType());
			baseProperties.setAttribute("actFlag", procProperties.getActFlag());
			baseProperties.setAttribute("waittime", procProperties.getWaittime());
			baseProperties.setAttribute("isSltTrans", procProperties.getIsSltTrans());
			baseProperties.setAttribute("isSameCredit", procProperties.getIsSameCredit());

			vmlProperties = new Element("VMLProperties");
			vmlProperties.setAttribute("shapetype", procProperties.getShapetype());
			vmlProperties.setAttribute("width", String.valueOf(procProperties.getWidth()));
			vmlProperties.setAttribute("height", String.valueOf(procProperties.getHeight()));
			vmlProperties.setAttribute("x", procProperties.getX() + "px");
			vmlProperties.setAttribute("y", procProperties.getY() + "px");
			vmlProperties.setAttribute("textWeight", procProperties.getTextWeight());
			vmlProperties.setAttribute("strokeWeight", procProperties.getStrokeWeight());
			vmlProperties.setAttribute("zIndex", procProperties.getZIndex());


			proc.addContent(baseProperties);
			proc.addContent(vmlProperties);

			procs.addContent(proc);
		}

		// 生成step
		steps = new Element("Steps");
		topFlow.addContent(steps);

		for (int i = 0; i < stepList.size(); i++) {
			step = new Element("Step");

			StepProperties stepProperties = (StepProperties) stepList.get(i);
			baseProperties = new Element("BaseProperties");
			baseProperties.setAttribute("id", stepProperties.getId());
			baseProperties.setAttribute("text", stepProperties.getText());
			baseProperties.setAttribute("from", stepProperties.getFrom());
			baseProperties.setAttribute("to", stepProperties.getTo());
			baseProperties.setAttribute("cond", stepProperties.getCond());

			vmlProperties = new Element("VMLProperties");
			vmlProperties.setAttribute("points", stepProperties.getPoints());
			vmlProperties.setAttribute("fromRelX", stepProperties.getFromRelX());
			vmlProperties.setAttribute("fromRelY", stepProperties.getFromRelY());
			vmlProperties.setAttribute("toRelX", stepProperties.getToRelX());
			vmlProperties.setAttribute("toRelY", stepProperties.getToRelY());
			vmlProperties.setAttribute("shapetype", stepProperties.getShapetype());
			vmlProperties.setAttribute("startArrow", stepProperties.getStartArrow());
			vmlProperties.setAttribute("endArrow", stepProperties.getEndArrow());
			vmlProperties.setAttribute("strokeWeight", stepProperties.getStrokeWeight());
			vmlProperties.setAttribute("zIndex", stepProperties.getZIndex());
			vmlProperties.setAttribute("inset", stepProperties.getInset());

			step.addContent(baseProperties);
			step.addContent(vmlProperties);

			steps.addContent(step);
		}

		XMLOutputter XMLOut = new XMLOutputter();

		String outputString = XMLOut.outputString(Doc);

		return outputString;
	}

	/**
	 * 将字符串反序列化成对象
	 *
	 * @param text
	 * @return
	 * @throws Exception
	 */
	private String xmlStringToFile(String xmlString) throws Exception {

		// 创建输出文件
		File file = null;

		try {
			// 创建输出文件
			file = new File(this.filePath + this.systemId + ".xml");

			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}

			// 以utf-8编码写文件
			// JDK 1.5才有的方法
			// PrintWriter pw = new PrintWriter(file, "UTF-8");
			// pw.write(xmlString);
			// pw.close();

			FileOutputStream fileWriter = new FileOutputStream(file);
			OutputStreamWriter outStreamWriter = new OutputStreamWriter(
					fileWriter, "UTF-8");
			PrintWriter print = new PrintWriter(outStreamWriter);
			print.write(xmlString);
			print.close();


		} catch (Exception e) {
			e.printStackTrace();
		}

		return file.getName();
	}

	/**
	 * 保存图形
	 * @param xmlString
	 * @param systemId
	 * @throws Exception
	 */
	public void saveGraphics(String xmlString) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String sql = " update asdb.k_customergraphics set xml=? "
					   + " where systemId=? ";

			ps = conn.prepareStatement(sql);
			ps.setString(1, xmlString);
			ps.setString(2, this.systemId);

			if(ps.executeUpdate() > 0) {
				xmlStringToFile(xmlString);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
	}

	/**
	 * 重置合并报表图形
	 * @throws Exception
	 */
	public void resetGraphics() throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String sql = " delete from asdb.k_customergraphics "
					   + " where systemId=? ";

			ps = conn.prepareStatement(sql);
			ps.setString(1, this.systemId);

			ps.executeUpdate();

			File file = new File(this.filePath + this.systemId + ".xml");

			file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Connection conn = null;
		try {

			String url = "jdbc:mysql://192.168.1.2:5188/asdb?characterEncoding=GBK";
			String userName = "xoops_root";
			String password = "654321";

			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url, userName, password);
			//new ReportGraphics(conn).generateNet("7");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
	}
}
