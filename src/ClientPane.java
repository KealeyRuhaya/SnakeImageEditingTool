import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Base64;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ClientPane extends GridPane{
	
	//GUI
		private Label lblHeading;
	
		private Button btnConnect;
		
		private Button btnChooseImage;
		private Label lblImageName;
		
		private Button btnOptimize;
		private Label lblOptimizeInstruction;
		
		private TextArea txtServerResponse;
		
		private Label lblOrg;
		private ImageView imgViewOrg;
		private Label lblOpt;
		private ImageView imgViewOpt;
		
		
		//Streams
			Socket clientsocket;
			
			InputStream is;
			BufferedReader br;
			
			DataOutputStream dos;
			BufferedOutputStream bos;
			OutputStream os;
		//variables 
			private File selectedFile = null;
			private String dilationUrl = "/api/Dilation";
			private String cropUrl = "/api/Crop";
			private String ORBUrl = "/api/ORB";
			
	
	
	public ClientPane(Stage primaryStage) {
		setupUI();
		
		btnConnect.setOnAction((event)->{
			connect();
			
		});
		
		
		btnChooseImage.setOnAction((event)->{
			
			FileChooser fc = new FileChooser();
			selectedFile = fc.showOpenDialog(null);
			
			if(selectedFile!=null) {
				//display image selected
				Image selected = new Image(selectedFile.getAbsolutePath());
				imgViewOrg.setImage(selected);
				txtServerResponse.appendText("Image Selected!\r\n");
				lblImageName.setText("File Name: " + selectedFile.getName());
			}else {
				txtServerResponse.appendText("Image NOT Selected!\r\n");
				lblImageName.setText("Awaiting Image to be chosen...");
			}
			
		});
		
		
		btnOptimize.setOnAction((event)->{
			if(selectedFile!=null) {
				
				optimiseImage();
			}else {
				txtServerResponse.appendText("Image HAS NOT been selected!\r\n");
			}
			
			
		});
		
		
	}
	
	public void optimiseImage() {
		File currentFile = null;
		currentFile = dilateImage();
		if(currentFile!=null) {
			connect();
			currentFile = cropImage(currentFile);
		}else {
			txtServerResponse.appendText("Current File NOT Dilated\r\n");
		}
		
		if(currentFile!=null) {
			connect();
			currentFile = ORBImage(currentFile);
		}else {
			txtServerResponse.appendText("Current File NOT Cropped\r\n");
		}
		
		
		
	}
	
	public File cropImage(File currentFile) {
		File returnFile = null;
		selectedFile = currentFile;
		try {
			//sending request 
			String encodedFile = null;
			FileInputStream fis = new FileInputStream(selectedFile);
			byte bytes[] = new byte[(int)selectedFile.length()];
			fis.read(bytes);
			//encoding the file into base64 format
			encodedFile = new String(Base64.getEncoder().encodeToString(bytes));
			byte[] bytesToSend = encodedFile.getBytes();
			
			//Sending the POST request using dos
			dos.write(("POST " + cropUrl + " HTTP/1.1\r\n").getBytes());
			dos.write(("Content-Type: " + "application/text\r\n").getBytes());
			dos.write(("Content-Length: " +encodedFile.length() + "\r\n").getBytes());
			dos.write(("\r\n").getBytes());
			dos.write(bytesToSend);
			dos.flush();
			dos.write(("\r\n").getBytes());
			
			txtServerResponse.appendText("POST Command Sent(dilation)\r\n");
			
			//Recieving a response
			//Header
			String response = "";
			String line = ""; 
			
			while(!(line = br.readLine()).equals("")) {
				
				response += line + "\n";
				
			}
			System.out.println(response);
			
			//Receiving the Image data
			String imgData = "";
			while((line = br.readLine())!=null) {
				imgData += line;
			}
			System.out.println(imgData);
			
			//getting the image data only
			String dilatedBase64Str = imgData.substring(imgData.indexOf('\'')+1, imgData.lastIndexOf('}')-1);
			System.out.println(dilatedBase64Str);
			
			//decode base64 to Image
			byte[] decodedStr = Base64.getDecoder().decode(dilatedBase64Str);
			
			//Display the dilated image 
			Image dilatedImage = new Image(new ByteArrayInputStream(decodedStr));
			//imgViewOpt.setImage(dilatedImage);
			returnFile = new File("data\\current\\currentImage2.jpg");
			ImageIO.write(SwingFXUtils.fromFXImage(dilatedImage, null), "jpg", returnFile);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return returnFile;
		
	}
	
	public File dilateImage() {
		File newFile = null;
		try {
			//sending request 
			String encodedFile = null;
			FileInputStream fis = new FileInputStream(selectedFile);
			byte bytes[] = new byte[(int)selectedFile.length()];
			fis.read(bytes);
			//encoding the file into base64 format
			encodedFile = new String(Base64.getEncoder().encodeToString(bytes));
			byte[] bytesToSend = encodedFile.getBytes();
			
			//Sending the POST request using dos
			dos.write(("POST " + dilationUrl + " HTTP/1.1\r\n").getBytes());
			dos.write(("Content-Type: " + "application/text\r\n").getBytes());
			dos.write(("Content-Length: " +encodedFile.length() + "\r\n").getBytes());
			dos.write(("\r\n").getBytes());
			dos.write(bytesToSend);
			dos.flush();
			dos.write(("\r\n").getBytes());
			
			txtServerResponse.appendText("POST Command Sent(dilation)\r\n");
			
			//Recieving a response
			//Header
			String response = "";
			String line = ""; 
			
			while(!(line = br.readLine()).equals("")) {
				
				response += line + "\n";
				
			}
			System.out.println(response);
			
			//Receiving the Image data
			String imgData = "";
			while((line = br.readLine())!=null) {
				imgData += line;
			}
			System.out.println(imgData);
			
			//getting the image data only
			String dilatedBase64Str = imgData.substring(imgData.indexOf('\'')+1, imgData.lastIndexOf('}')-1);
			System.out.println(dilatedBase64Str);
			
			//decode base64 to Image
			byte[] decodedStr = Base64.getDecoder().decode(dilatedBase64Str);
			
			//Display the dilated image 
			Image dilatedImage = new Image(new ByteArrayInputStream(decodedStr));
			//imgViewOpt.setImage(dilatedImage);
			newFile = new File("data\\current\\currentImage1.jpg");
			ImageIO.write(SwingFXUtils.fromFXImage(dilatedImage, null), "jpg", newFile);
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return newFile;
		
	}
	
	
	public File ORBImage(File selecFile) {
		File returnFile = null;
		selectedFile = selecFile;
		try {
			//sending request 
			String encodedFile = null;
			FileInputStream fis = new FileInputStream(selectedFile);
			byte bytes[] = new byte[(int)selectedFile.length()];
			fis.read(bytes);
			//encoding the file into base64 format
			encodedFile = new String(Base64.getEncoder().encodeToString(bytes));
			byte[] bytesToSend = encodedFile.getBytes();
			
			//Sending the POST request using dos
			dos.write(("POST " + ORBUrl + " HTTP/1.1\r\n").getBytes());
			dos.write(("Content-Type: " + "application/text\r\n").getBytes());
			dos.write(("Content-Length: " +encodedFile.length() + "\r\n").getBytes());
			dos.write(("\r\n").getBytes());
			dos.write(bytesToSend);
			dos.flush();
			dos.write(("\r\n").getBytes());
			
			txtServerResponse.appendText("POST Command Sent(ORB)\r\n");
			
			//Recieving a response
			//Header
			String response = "";
			String line = ""; 
			
			while(!(line = br.readLine()).equals("")) {
				
				response += line + "\n";
				
			}
			System.out.println(response);
			
			//Receiving the Image data
			String imgData = "";
			while((line = br.readLine())!=null) {
				imgData += line;
			}
			System.out.println(imgData);
			
			//getting the image data only
			String ORBBase64Str = imgData.substring(imgData.indexOf('\'')+1, imgData.lastIndexOf('}')-1);
			System.out.println(ORBBase64Str);
			
			//decode base64 to Image
			byte[] decodedStr = Base64.getDecoder().decode(ORBBase64Str);
			
			//Display the dilated image 
			Image ORBImage = new Image(new ByteArrayInputStream(decodedStr));
			imgViewOpt.setImage(ORBImage);
			returnFile = new File("data\\current\\currentImage3.jpg");
			ImageIO.write(SwingFXUtils.fromFXImage(ORBImage, null), "jpg", returnFile);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return returnFile;
		
	}
	
	
	public void connect() {
		

		//Client connecting to server 
		try {
			clientsocket = new Socket("localhost", 5000);
			//Setup of Streams for Receiving text 
			is = clientsocket.getInputStream();
			br = new BufferedReader(new InputStreamReader(is));
			
			//Setup of Streams for Sending Images
			os = clientsocket.getOutputStream();
			bos = new BufferedOutputStream(os);
			dos = new DataOutputStream(bos);
			
			txtServerResponse.appendText("Successfully Connected to the server!\r\n");
			
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}
	public void setupUI() {
		setAlignment(Pos.CENTER);
		setHgap(10);
		setVgap(10);
		
		lblHeading = new Label("This program will allow you to choose an image of a Snake to be identified. Then it will optimize the image and identify whether it is poisonous or not.");
		
		btnConnect = new Button("Connect");
		
		btnChooseImage = new Button("Choose Image");
		lblImageName = new Label("Awaiting Image to be chosen...");
		
		btnOptimize = new Button("Optimize Image");
		lblOptimizeInstruction = new Label("<----Please click this Button to Optimize selected image");
		
		txtServerResponse = new TextArea("From Server:\r\n");
		txtServerResponse.setPrefHeight(200);
		
		imgViewOrg = new ImageView();
		imgViewOrg.setFitHeight(200);
		imgViewOrg.setFitWidth(200);
		imgViewOpt = new ImageView();
		imgViewOpt.setFitHeight(200);
		imgViewOpt.setFitWidth(200);
		
		lblOrg = new Label("ORIGINAL IMAGE");
		lblOpt = new Label("OPTIMISED IMAGE");
		
		add(lblHeading,0,0,6,1);
		
		add(btnConnect,0,1);
		
		add(btnChooseImage,0,2);
		add(lblImageName,1,2);
		
		add(btnOptimize, 0, 3);
		add(lblOptimizeInstruction,1,3);
		
		add(txtServerResponse, 0, 4,6,1);
		
		add(lblOrg, 0, 5,3,1);
		add(lblOpt, 4, 5,3,1);
		
		add(imgViewOrg, 0, 6,3,1);
		add(imgViewOpt, 4, 6,3,1);
		
		
	}
	
	

}
