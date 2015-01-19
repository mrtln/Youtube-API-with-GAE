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


import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.*;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;






public class MyVideos extends HttpServlet {

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
        	//
        	YouTube.Channels.List channelRequest = youtube.channels().list("contentDetails");
            channelRequest.setMine(true);

            channelRequest.setFields("items/contentDetails,nextPageToken,pageInfo");
            ChannelListResponse channelResult = channelRequest.execute();
            List<Channel> channelsList = channelResult.getItems();
            if (channelsList != null) {
              // Gets user's default channel id (first channel in list).
              String uploadPlaylistId =
                  channelsList.get(0).getContentDetails().getRelatedPlaylists().getUploads();

              List<PlaylistItem> playlistItemList = new ArrayList<PlaylistItem>();

              
              /*
               * Now that we have the playlist id for your uploads, we will request the playlistItems
               * associated with that playlist id, so we can get information on each video uploaded. This
               * is the template for the list call. We call it multiple times in the do while loop below
               * (only changing the nextToken to get all the videos).
               * https://developers.google.com/youtube/v3/docs/playlistitems/list
               */
              YouTube.PlaylistItems.List playlistItemRequest =
                  youtube.playlistItems().list("id,contentDetails,snippet");
              playlistItemRequest.setPlaylistId(uploadPlaylistId);

              // This limits the results to only the data we need and makes things more efficient.
              playlistItemRequest.setFields(
                  "items(contentDetails/videoId,snippet/title,snippet/publishedAt),nextPageToken,pageInfo");
              String nextToken = "";

              // Loops over all search page results returned for the uploadPlaylistId.
             PrintWriter out = resp.getWriter();	
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Resultado</title>");
            out.println("</head>");
            out.println("<body>"); 
              do {
                  playlistItemRequest.setPageToken(nextToken);
                  PlaylistItemListResponse playlistItemResult = playlistItemRequest.execute();

                  playlistItemList.addAll(playlistItemResult.getItems());

                  nextToken = playlistItemResult.getNextPageToken();
                } while (nextToken != null);

               int size = playlistItemList.size();
               int i=0;
               
        	    while (i<size) {
        	    		i++;
        	    		PlaylistItem playlistItem = playlistItemList.iterator().next();
        		      out.println("<p> Título:" + playlistItem.getSnippet().getTitle() + "</p>");
        		      out.println("<p> <iframe width=560 height=315 src='//www.youtube.com/embed/"+playlistItem.getContentDetails().getVideoId()+"' frameborder=0 allowfullscreen></iframe></p>");
        		      out.println("<p> Fecha"+ playlistItem.getSnippet().getPublishedAt()+"</p>");
        		      playlistItemList.remove(0);	
        		    }
                	
        	    out.println("<br>"); 	

              // Prints results.
              
            }

     	   
        	  

            
       
         
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