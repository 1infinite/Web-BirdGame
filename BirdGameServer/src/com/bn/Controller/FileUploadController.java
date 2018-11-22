package com.bn.Controller;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.bn.Database.DBUtil;
import com.bn.Server.Room;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

@Controller
public class FileUploadController {
	@RequestMapping(value="/{formName}")
	 public String loginForm(@PathVariable String formName) {
		// ��̬��תҳ��
		return formName;
	}
	
	//�ϴ��ļ����Զ��󶨵�MultipartFile��
	 @RequestMapping(value="/upload",method=RequestMethod.POST)
	 public String upload(HttpServletRequest request,
			@RequestParam("file") MultipartFile file) throws Exception {
		
		String room_id = request.getParameter("room_id");
	    System.out.println("����:"+room_id);
	    //����ļ���Ϊ�գ�д���ϴ�·��
		if(!file.isEmpty()) {
			//�ϴ��ļ�·��
			String path = request.getServletContext().getRealPath("/loaded/");
			System.out.println("·��:"+path);
			//�ϴ��ļ���
			String originalName = file.getOriginalFilename();
			String type = originalName.substring(originalName.indexOf('.'), originalName.length());
			System.out.println("ͼƬ����:"+type);
			//���¶���ͼƬ����,����ʱ��������ļ�,�����λ�����
			String filename = room_id+"_"+System.currentTimeMillis()
				+"_"+(int)(Math.random()*9)+(int)(Math.random()*9)+type;
			System.out.println("�ļ�����:"+filename);
		    File filepath = new File(path,filename);
			//�ж�·���Ƿ���ڣ���������ھʹ���һ��
	        if (!filepath.getParentFile().exists()) { 
	        	filepath.getParentFile().mkdirs();
	        }
	        //���ϴ��ļ����浽һ��Ŀ���ļ�����
			file.transferTo(new File(path + File.separator + filename));
			DBUtil.insertSponsorLogo(room_id, "loaded/"+filename);
			return "success";
		}
		return null;
	 }
	 
	 @RequestMapping(value="/meetingForm")
	 public ModelAndView createMeeting(HttpServletRequest request,
			 @RequestParam("meeting_name") String meeting_name,
			 @RequestParam("start_time") String start_time,
			 @RequestParam("end_time") String end_time)throws Exception {
		
		 System.out.println(meeting_name+" "+start_time+" "+end_time);
		 
		 String room_id = "";
		 try {
			 room_id = Room.createMeetingRoom(meeting_name,Room.string2Time(start_time),Room.string2Time(end_time),"000");
		 }catch(ParseException e) {}
		 
		 System.out.println(room_id);
		 request.setAttribute("room_id",room_id);  
		 return new ModelAndView("uploadLogo","room_id",room_id);
	 }

}