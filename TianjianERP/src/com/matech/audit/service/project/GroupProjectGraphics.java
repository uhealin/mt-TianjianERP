package com.matech.audit.service.project;

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

public class GroupProjectGraphics {

	private int NODE_WIDTH = 150; // 节点宽
	private int NODE_HEIGHT = 60; // 节点高
	private int X = 200; // X轴递增
	private int Y = 70; // Y轴递增
	private String filePath = null;
	private String projectId = null;

	private Connection conn = null;

	public GroupProjectGraphics(Connection conn, String projectId) {
		this.conn = conn;
		this.projectId = projectId;

		try {
			this.filePath = org.del.DelPublic.getWarPath();
		} catch (Exception e) {
			this.filePath = org.del.DelPublic.getClassRoot() + "../../";
		}
		if (this.filePath.substring(0, 1).equals("/")) {
			this.filePath = this.filePath.substring(1);
		}
		if (!this.filePath.substring(this.filePath.length() - 1,
				this.filePath.length()).equals("/")) {
			this.filePath += "/";
		}

		this.filePath += "groupProject/graphics/data/";

		File file = new File(this.filePath);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	/**
	 * 删除关系图
	 * 
	 * @throws Exception
	 */
	public void removeGraphics() throws Exception {
		PreparedStatement ps = null;

		try {
			String sql = " delete from asdb.z_groupprojectgraphics "
					+ " where projectId=? ";

			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);

			if (ps.executeUpdate() > 0) {
				// 创建输出文件
				File file = new File(this.filePath + this.projectId + ".xml");

				if (file.exists()) {
					file.delete();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 保存图形
	 * 
	 * @param xmlString
	 * @param systemId
	 * @throws Exception
	 */
	public void saveGraphics(String xmlString) throws Exception {
		PreparedStatement ps = null;

		try {
			String sql = " update asdb.z_groupprojectgraphics set xml=? "
					+ " where projectId=? ";

			ps = conn.prepareStatement(sql);
			ps.setString(1, xmlString);
			ps.setString(2, this.projectId);

			if (ps.executeUpdate() > 0) {
				xmlStringToFile(xmlString);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 生成关系图
	 * 
	 * @param systemId
	 * @return
	 * @throws Exception
	 */
	public String generateGraphics() throws Exception {
		PreparedStatement ps = null;

		ResultSet rs = null;
		StepProperties stepProperties = null;
		String fileName = "";

		try {

			String sql = " select xml " + " from z_groupprojectgraphics "
					+ " where projectId=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);
			rs = ps.executeQuery();
			String xmlString = null;

			if (rs.next()) {
				xmlString = rs.getString(1);
			} else {

				try {
					// 重新计算全路径
					new GroupProjectService(conn).repairFullPath();
				} catch (Exception e) {
					e.printStackTrace();
				}

				List procList = generateNode();
				List stepList = new ArrayList();

				sql = " select a.parentprojectId,a.projectid,b.name "
						+ " from z_groupproject a left join k_user b "
						+ " on a.auditPeople=b.id "
						+ " where a.fullpath like concat('" + this.projectId
						+ "|','%') " + " order by a.fullpath ";

				ps = conn.prepareStatement(sql);

				rs = ps.executeQuery();

				int i = 0;
				while (rs.next()) {
					String fromProjectId = rs.getString(1);
					String toProjectId = rs.getString(2);
					String userName = rs.getString(3);

					// 上级项目坐标
					int[] fromXY = getXY(procList, fromProjectId);

					// 项目坐标
					int[] toXY = getXY(procList, toProjectId);

					if (fromXY[0] != -1 && fromXY[1] != -1 && toXY[0] != -1
							&& toXY[1] != -1) {

						int fromX = fromXY[0] + (this.NODE_WIDTH / 2);
						int fromY = fromXY[1] + this.NODE_HEIGHT;
						int toX = toXY[0];// + this.NODE_WIDTH;
						int toY = toXY[1] + (this.NODE_HEIGHT / 2);

						stepProperties = new StepProperties();
						stepProperties.setId(String.valueOf(i));
						stepProperties.setText(userName);
						stepProperties.setFrom(fromProjectId);
						stepProperties.setTo(toProjectId);
						stepProperties.setCond(String.valueOf(i) + "=\""
								+ String.valueOf(i) + "\"");

						String points = fromX + "," + fromY + "," + fromX + ","
								+ toY + "," + toX + "," + toY;

						String inset = (toX - fromX) / 2 / 4 * 3 + "pt,"
								+ (toY - fromY) / 2 / 4 * 3 + "pt";

						stepProperties.setPoints(points); // x,y,x1,y1,x2,y2
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

				xmlString = generateXmlFile(procList, stepList);

				sql = " insert into asdb.z_groupprojectgraphics(projectId,xml) "
						+ " values(?,?) ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, this.projectId);
				ps.setString(2, xmlString);
				ps.executeUpdate();
			}

			fileName = xmlStringToFile(xmlString);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return fileName;
	}

	/**
	 * 获得节点坐标
	 * 
	 * @param procList
	 * @param customerId
	 * @return
	 * @throws Exception
	 */
	private int[] getXY(List procList, String customerId) throws Exception {

		int[] xy = { -1, -1 };
		for (int i = 0; i < procList.size(); i++) {
			ProcProperties procProperties = (ProcProperties) procList.get(i);

			if (customerId.equals(procProperties.getId())) {
				xy[0] = procProperties.getX();// - (X / 2) - 2;
				xy[1] = procProperties.getY();// - (Y / 2) - 8;
			}
		}

		return xy;
	}

	/**
	 * 显示网状列出图
	 * 
	 * @param systemId
	 * @param procList
	 * @param parentProjectId
	 * @param x
	 * @param y
	 * @throws Exception
	 */
	private List generateNode() throws Exception {
		List procList = new ArrayList();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String projectId = null;
		String projectName = null;
		ProcProperties procProperties = null;

		try {

			String sql = " select projectid,projectName,parentprojectId,level0 "
					+ " from z_groupproject  "
					+ " where fullpath like concat('"
					+ this.projectId
					+ "|','%') " + " order by fullpath ";

			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int y = 0;
			int x = 50;

			while (rs.next()) {
				projectId = rs.getString(1);
				projectName = rs.getString(2);
				int level = rs.getInt(4);

				procProperties = new ProcProperties();
				procProperties.setId(projectId);
				procProperties.setText("<strong>" + projectName + "["
						+ projectId + "]</strong>");

				if (level == 0) {
					procProperties.setProcType("BeginProc");
				} else {
					procProperties.setProcType("NormalProc");
				}

				procProperties.setActFlag("1010");
				procProperties.setWaittime("");
				procProperties.setIsSltTrans("1");
				procProperties.setIsSameCredit("0");
				procProperties.setShapetype("Rect");
				procProperties.setWidth(this.NODE_WIDTH);
				procProperties.setHeight(this.NODE_HEIGHT);
				procProperties.setX(x + level * this.X);
				procProperties.setY(y += this.Y);
				procProperties.setTextWeight("9pt");
				procProperties.setStrokeWeight("1");
				procProperties.setZIndex("1");
				procList.add(procProperties);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return procList;
	}

	// public void update

	// private int getChildCount() throws

	/**
	 * 生成XML文件
	 * 
	 * @param procList
	 * @param stepList
	 * @throws Exception
	 */
	private String generateXmlFile(List procList, List stepList)
			throws Exception {
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
		topFlow.setAttribute("text", "集团项目关系图");
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
			baseProperties.setAttribute("procType", procProperties
					.getProcType());
			baseProperties.setAttribute("actFlag", procProperties.getActFlag());
			baseProperties.setAttribute("waittime", procProperties
					.getWaittime());
			baseProperties.setAttribute("isSltTrans", procProperties
					.getIsSltTrans());
			baseProperties.setAttribute("isSameCredit", procProperties
					.getIsSameCredit());

			vmlProperties = new Element("VMLProperties");
			vmlProperties.setAttribute("shapetype", procProperties
					.getShapetype());
			vmlProperties.setAttribute("width", String.valueOf(procProperties
					.getWidth()));
			vmlProperties.setAttribute("height", String.valueOf(procProperties
					.getHeight()));
			vmlProperties.setAttribute("x", procProperties.getX() + "px");
			vmlProperties.setAttribute("y", procProperties.getY() + "px");
			vmlProperties.setAttribute("textWeight", procProperties
					.getTextWeight());
			vmlProperties.setAttribute("strokeWeight", procProperties
					.getStrokeWeight());
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
			vmlProperties
					.setAttribute("fromRelX", stepProperties.getFromRelX());
			vmlProperties
					.setAttribute("fromRelY", stepProperties.getFromRelY());
			vmlProperties.setAttribute("toRelX", stepProperties.getToRelX());
			vmlProperties.setAttribute("toRelY", stepProperties.getToRelY());
			vmlProperties.setAttribute("shapetype", stepProperties
					.getShapetype());
			vmlProperties.setAttribute("startArrow", stepProperties
					.getStartArrow());
			vmlProperties
					.setAttribute("endArrow", stepProperties.getEndArrow());
			vmlProperties.setAttribute("strokeWeight", stepProperties
					.getStrokeWeight());
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
			file = new File(this.filePath + this.projectId + ".xml");

			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}

			if (file.exists()) {
				file.delete();
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
	 * @param args
	 */
	public static void main(String[] args) {
		Connection conn = null;
		try {

			String url = "jdbc:mysql://127.0.0.1:5188/asdb?characterEncoding=GBK";
			String userName = "xoops_root";
			String password = "654321";

			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url, userName, password);

			new GroupProjectService(conn).repairFullPath();

			// list.add("20089220");
			// new GroupProjectGraphics(conn,"20089220").test(list,"20089220");

			// for(int i=0; i < list.size(); i++) {
			// System.out.println(list.get(i));
			// }

			new GroupProjectGraphics(conn, "20089220").generateGraphics();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
	}
}
