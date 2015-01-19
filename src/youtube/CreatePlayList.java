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
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.PlaylistSnippet;
import com.google.api.services.youtube.model.PlaylistStatus;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;








import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;




public class CreatePlayList extends HttpServlet {

	  /** Global instance of the HTTP transport. */
	  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

	  /** Global instance of the JSON factory. */
	  private static final JsonFactory JSON_FACTORY = new JacksonFactory();

	  /** Global instance of Youtube object to make all API requests. */
	  private static YouTube youtube;


	  
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {
        	String clientId="951079454675-vt42t3ih4phb02bctqee5gcbp8if3irg.apps.googleusercontent.com";
        	String clientSecret="v2AnaZFke6l9qybJqUmQwMD5";
        	String refreshToken="1/9WSp6tdnZftQxNhsDZvgvhcmf1kB1VNK-i5-i5m8ieUMEudVrK5jSpoR30zcRFq6";
        	
        	Credential credential = new GoogleCredential.Builder().setTransport(new NetHttpTransport()).setJsonFactory(new JacksonFactory()).setClientSecrets(clientId, clientSecret).build();
        	credential.setRefreshToken(refreshToken);

        	youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(
        	          "API youtube").build();
        	
        	
        	//Creamos la lista de reproducción
       	
        	Playlist playList = new Playlist();
        	PlaylistSnippet playlistSnippet = new PlaylistSnippet();
        	playlistSnippet.setTitle("Test Playlist via JAVA " + Calendar.getInstance().getTime());
        	PlaylistStatus playlistStatus = new PlaylistStatus();
            playlistStatus.setPrivacyStatus("public");
            playList.setStatus(playlistStatus);
        	playList.setSnippet(playlistSnippet);        	
        	YouTube.Playlists.Insert playListInsert= youtube.playlists().insert("snippet,status", playList);
        	Playlist playL = playListInsert.execute();		
        	
        	añadirItem(req, 1, playL.getId());
        	añadirItem(req, 2, playL.getId());
        	añadirItem(req, 3, playL.getId());
        	añadirItem(req, 4, playL.getId());
     	   
        	PrintWriter out = resp.getWriter();	  
        	out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Resultado</title>");
            out.println("</head>");
            out.println("<body>");  
            out.println("<p><iframe width=560 height=315 src='//www.youtube.com/embed/videoseries?list="+playL.getId()+"' frameborder=0 allowfullscreen></iframe></p>");
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
    
  public void añadirItem(HttpServletRequest req, int id, String PlaylistId){
	  try{
  	//Una vez creada añadimos el video
  	//Creamos el item que añadiremos
  	PlaylistItem playlistItem = new PlaylistItem();

  	String videoId="";
  	videoId=req.getParameter("id"+id);
  	//El item que se añadirá tiene como detalles el objeto Snippet que lleva el id del video, título e id de la playlist a añadir
  	PlaylistItemSnippet playlistItemSnippet = new PlaylistItemSnippet();
  	
  	playlistItemSnippet.setPlaylistId(PlaylistId);
  	
  	ResourceId rid = new ResourceId();
  	rid.setKind("youtube#video");
  	rid.setVideoId(videoId);
  	playlistItemSnippet.setResourceId(rid);
  	playlistItemSnippet.setTitle("Test título via Java");
  	playlistItem.setSnippet(playlistItemSnippet);
  	
      YouTube.PlaylistItems.Insert playlistItemsInsertCommand =
              youtube.playlistItems().insert("snippet,contentDetails", playlistItem);
      playlistItemsInsertCommand.execute();
	  
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
  }
}