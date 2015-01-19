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

import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ComputacionServlet extends HttpServlet {

  private static final String API_KEY = "AIzaSyDTNhhNy-qRZmQU-oAGdUMLPzfdbVX4uKE";

  private static final long serialVersionUID = 1;

  /** Global instance of the max number of videos we want returned (50 = upper limit per page). */
  private static final long NUMBER_OF_VIDEOS_RETURNED = 10;

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    HttpTransport httpTransport = new UrlFetchTransport();
    JsonFactory jsonFactory = new JacksonFactory();
   
        try {
         
         YouTube youtube = new YouTube.Builder(httpTransport, jsonFactory, null).setApplicationName("YouTube API basic")
        	 .setGoogleClientRequestInitializer(new YouTubeRequestInitializer(API_KEY)).build();
         
          String queryTerm = "";
          queryTerm = req.getParameter("input");
          
          if(queryTerm.length()<1){//Valor por defecto si no introducimos nada
        	  queryTerm = "YouTube Developers Live";         	  
          }
                    
          YouTube.Search.List search = youtube.search().list("id,snippet");

          search.setKey(API_KEY);
          search.setQ(queryTerm);
          search.setType("video");
          search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
          search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
          SearchListResponse searchResponse = search.execute();

          List<SearchResult> searchResultList = searchResponse.getItems();

          if (searchResultList != null) {
            prettyPrint(searchResultList.iterator(), queryTerm, resp);
        	
          }
        } catch (GoogleJsonResponseException e) {
          System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
              + e.getDetails().getMessage());
        } catch (IOException e) {
          System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
          t.printStackTrace();
        }
    }//end doGet
    
  
  private static void prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query, HttpServletResponse resp) 
		  throws IOException {
	  
	    resp.setContentType("text/html");
	    resp.setStatus(200);
	    PrintWriter out = resp.getWriter();	   
	    out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Resultado</title>");
        out.println("</head>");
        out.println("<body>");       
        out.println("<p>=============================================================</p>");
	    out.println("<h2>First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".</h2>");
	    out.println("<p>=============================================================</p>");
	    out.println("<h4>If you want to see them, please click the video Id.</h4>");
	    out.println("<br>");
	    if (!iteratorSearchResults.hasNext()) {
	    	 out.println("<p>There aren't any results for your query.</p>");
	    }        
	    
	    while (iteratorSearchResults.hasNext()) {

		      SearchResult singleVideo = iteratorSearchResults.next();
		      ResourceId rId = singleVideo.getId();

		      // Double checks the kind is video.
		      if (rId.getKind().equals("youtube#video")) {
		        Thumbnail thumbnail = (Thumbnail) singleVideo.getSnippet().getThumbnails().get("default");
		       
		        out.println("<p><strong>Title:</strong> " + singleVideo.getSnippet().getTitle()+"</p>");	
		        out.println("<p> <iframe width=560 height=315 src='//www.youtube.com/embed/"+rId.getVideoId()+"' frameborder=0 allowfullscreen></iframe></p>");	
		        out.println("<p><strong>Video Id:</strong> "
		        			+"<a href=\"https://www.youtube.com/watch?v="+rId.getVideoId()+"\""
		        			+ "target=\"_blank\">"+rId.getVideoId()+"</a></p>");     		         
		        out.println("<p><strong>Thumbnail:</strong> " + thumbnail.getUrl()+"</p>");
		        out.println("<p>-------------------------------------------------------------</p>");
		      }
		 }	    
	    out.println("<p>Volver al <a href=\""+"search.html\""+">buscador de videos</a></p>");
        out.println("</body>");
        out.println("</html>");   
	}
  
    
}

