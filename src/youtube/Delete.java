/*
 * Copyright (c) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package youtube;


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;




public class Delete extends HttpServlet {

	  /** Global instance of the HTTP transport. */
	  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

	  /** Global instance of the JSON factory. */
	  private static final JsonFactory JSON_FACTORY = new JacksonFactory();

	  /** Global instance of Youtube object to make all API requests. */
	  private static YouTube youtube;


	  
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {
        	String clientId="951079454675-vt42t3ih4phb02bctqee5gcbp8if3irg.apps.googleusercontent.com";
        	String clientSecret="v2AnaZFke6l9qybJqUmQwMD5";
        	String refreshToken="1/9WSp6tdnZftQxNhsDZvgvhcmf1kB1VNK-i5-i5m8ieUMEudVrK5jSpoR30zcRFq6";
        	
        	Credential credential = new GoogleCredential.Builder().setTransport(new NetHttpTransport()).setJsonFactory(new JacksonFactory()).setClientSecrets(clientId, clientSecret).build();
        	credential.setRefreshToken(refreshToken);

        	youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(
        	          "API youtube").build();
        	
        	String id = "";
            id = req.getParameter("id");
        	
        	YouTube.Videos.Delete videoDelete = youtube.videos().delete(id);
        	
        	videoDelete.execute();
     	   
        	PrintWriter out = resp.getWriter();	  
        	out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Resultado</title>");
            out.println("</head>");
            out.println("<p>Video Eliminado!</p>");
            out.println("<body>");  
    	    out.println("<br>");
       
         
  } catch (GoogleJsonResponseException e) {
	  System.err.println("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : "
	  + e.getDetails().getMessage());
	  e.printStackTrace();
	  } catch (IOException e) {
	  System.err.println("IOException: " + e.getMessage());
	  e.printStackTrace();
	  } catch (Throwable t) {
	  System.err.println("Throwable: " + t.getMessage());
	  t.printStackTrace();
	  }
        
    }//end doGet
    
}