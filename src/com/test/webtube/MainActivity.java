package com.test.webtube;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.test.webtube.R.menu;
import android.view.Menu;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnLongClickListener;
import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;

public class MainActivity extends Activity {

	WebView mWebView;
	String newurl;
	protected static final int MENU_SHARE = Menu.FIRST;
	protected static final int MENU_PLAY = Menu.FIRST + 1;
	protected static final int MENU_DOWNLOAD = Menu.FIRST + 2;
	protected static final int MENU_LINKTO = Menu.FIRST + 3;

	private int PORT_NUMBER = 4040;  
	 private int port = 4040;  
	 private ClientSocket cs = null; 
	 String str_ipaddr = null;
	 String pc_str_ipaddr = null;
	 String pad_str_ipaddr = null;
	 boolean found_server;
	 static int found_count;	
	 static String[] server_found_ip = new String[255];
	 static String[] thisip_x = new String[255];
	 DocumentBuilder builder    = null;
	 Document document    = null;
	 NodeList nod        = null; 
	 private static  Node  ele_1  = null; 
	 
	 private int web_action = 2;   // action 1=找video ,  2=找Image
	 String keyword = "";
	 String Touchlink_url = "";
	 String area_str = "";
	 WebView wv;
	 boolean show_menu;
	 String t1 = null;
	 String src_url="";
	 final String SERVERIP_SETTINGFILE = "/sdcard/server3.txt";
	 final String LOCAL_DOWNLOAD_DIR = "/sdcard/";
	 
	 DatagramSocket socket;
 	 TextView txt1, txt2, txt3;
 	 String MyIP; 
 	 DatagramPacket packet;
	 private Handler handler = new Handler();
 	 private String  KEYWORD_SEARCH = "Searching Client";
 	 private String  KEYWORD_IAMHERE = "I am Here";
 	 final int BROADCAST_PORT = 1828;
 	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (getIntent().hasExtra( "Youku" )) {
        
        	keyword = getIntent().getStringExtra( "Youku" );
        
        //	Toast.makeText(getApplicationContext(),
        //			keyword, Toast.LENGTH_SHORT)
		//			.show();
        	web_action = 1;
        }
		else if (getIntent().hasExtra( "Youtube" )) {
	        
        	keyword = getIntent().getStringExtra( "Youtube" );
        
        //	Toast.makeText(getApplicationContext(),
        //			keyword, Toast.LENGTH_SHORT)
		//			.show();
        	web_action = 2;
        }
		
		 pad_str_ipaddr = null;
	     pc_str_ipaddr = null;
	     
	       Get_Host_IP( "/sdcard/workserver.xml");
	       pad_str_ipaddr = readFileSdcard( SERVERIP_SETTINGFILE );
	   /*    
	       found_server = Port_Scan( PORT_NUMBER );
	       if( found_server )
	       {
	        	pad_str_ipaddr = server_found_ip[0];
	        	pc_str_ipaddr = server_found_ip[0];
	       }
	   */    
	       
		mWebView = (WebView) findViewById(R.id.webView1);

		// Button b1 = (Button) findViewById(R.id.button1);

		// final EditText et = (EditText) findViewById(R.id.editText1);

		mWebView.getSettings().setJavaScriptEnabled(true);

		mWebView.requestFocus();

		// final ProgressDialog progressDialog = new ProgressDialog(
		// MainActivity.this );

		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
			
			//	Toast.makeText(getApplicationContext(),
			//			"shouldOverrideUrlLoading" + url, Toast.LENGTH_SHORT)
			//			.show();
				// view.loadUrl(url);
				// mWebView.loadUrl(url);
				// return true;
//				if( Touchlink_url.regionMatches(thisStart, string, start, length)  .matches("http://)   .isEmpty()
			
			Touchlink_url = url;
			
			if(  web_action == 1)
			{
				
			
				String keystr="http://v.youku.com/v_show/id_";
				
				int search_index = Touchlink_url.indexOf("search_video");
				int search_playlist_index = Touchlink_url.indexOf("search_playlist");
				int newtop_index = Touchlink_url.indexOf("newtop");
				int nisearch_index = Touchlink_url.indexOf("nisearch");
				int channel_index = Touchlink_url.indexOf("channel");
				int u_search_index = Touchlink_url.indexOf("u_search");
				int keyword_index = Touchlink_url.indexOf("v?keyword");
				int tudou_home = Touchlink_url.indexOf("http://www.tudou.com/");
				int youku_com = Touchlink_url.indexOf("http://www.youku.com/");
				int detail_show = Touchlink_url.indexOf("/detail/show/");
				int tudou_programs = Touchlink_url.indexOf("http://www.tudou.com/programs/");
				
	/*   // ---- 其他進入 ------
	 * http://movie.tudou.com/
http://tv.tudou.com/
http://imake.tudou.com/
http://zy.tudou.com/
http://music.tudou.com/
http://www.tudou.com/listplay/0IuikEHu9s8.html
http://www.tudou.com/albumplay/dftaqaTPT7Q.html
     */			
				
				int index = Touchlink_url.indexOf(".baidu.com");
				
				if( Touchlink_url.length() > keystr.length() )
					area_str= Touchlink_url.substring(0, keystr.length());
				else
					area_str ="";
				
				show_menu = false;
				if( keystr.matches( area_str) )
				{
						show_menu = true;
						openContextMenu( mWebView );
						wv = view;
				}                              //===== 有以下關鍵字者 , 直接讓user進入 ======  
				else if( search_index != -1) //---------- 範例 http://www.soku.com/search_video/q_%E5%BC%A0%E9%9B%A8%E7%94%9F_orderby_1_page_2
					view.loadUrl(url);
				else if( search_playlist_index != -1) //---------- 範例 http://www.soku.com/search_playlist/q_%E5%BC%A0%E9%9B%A8%E7%94%9F
					view.loadUrl(url);
				else if( newtop_index != -1) //---------- 範例http://www.soku.com/newtop/all.html
					view.loadUrl(url);
				else if( nisearch_index != -1) //---------- 範例http://www.soku.com/t/nisearch/samsung
					view.loadUrl(url);		
				else if( channel_index != -1) //---------- 範例http://www.soku.com/channel/movie______1.html
					view.loadUrl(url);	
				else if( u_search_index != -1) //---------- 範例http://i.youku.com/u_search/q_%E5%85%85%E7%94%B5
					view.loadUrl(url);	
				else if( keyword_index != -1) //---------- 範例http://www.soku.com/v?keyword=%E5%85%85%E7%94%B5
					view.loadUrl(url);
				else if( tudou_programs != -1) //---------- 範例http://www.tudou.com/program/
				{    
					show_menu = true;
					openContextMenu( mWebView );
					wv = view;
				}	
				else if( tudou_home != -1) //---------- 範例http://www.tudou.com/
					view.loadUrl(url);	
				else if( youku_com != -1) //---------- 範例http://www.youku.com/
					view.loadUrl(url);	
				else if( detail_show != -1) //---------- 範例 http://www.soku.com/detail/show/XNjc0MDI4
					view.loadUrl(url);	
				else if( index == -1)
				{    
						show_menu = true;
						openContextMenu( mWebView );
						wv = view;
				}
				
			} //if(  web_action == 1)
			else
			{
				 String keystr="http://v.youku.com/v_show/id_";
					if( Touchlink_url.length() > keystr.length() )
						area_str= Touchlink_url.substring(0, keystr.length());
					
					show_menu = false;
					if( keystr.matches( area_str) )
					{
							show_menu = true;
							openContextMenu( mWebView );
							wv = view;
					}
					
				
					if (url.startsWith("http://m.youtube")) 
					{
						int n = url.indexOf("?");
							if (n > 0) {
								Uri sss;
								sss = Uri.parse(String.format(
										"http://www.youtube.com/v/%s",
										url.substring("vnd.youtube:".length(), n)));
						  }
							
				    }
				
				if( show_menu == false)
					view.loadUrl(url);
				
			} //else
			
			return true;
  }

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {

				// progressDialog.setMessage("Please wait...");
				// progressDialog.show();

				// Toast.makeText(getApplicationContext(),
				// "onPageStarted: "+url, Toast.LENGTH_SHORT).show();
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// if (progressDialog.isShowing()) {
				// progressDialog.dismiss();
				// }

				// Toast.makeText(getApplicationContext(),
				// "onPageFinished: "+url, Toast.LENGTH_SHORT).show();
				newurl = url;
				super.onPageFinished(view, url);
			}
			
            /*
			//@Override 
			public boolean onKeyDown(int keyCode, KeyEvent event) 
			{     
				if (keyCode == KeyEvent.KEYCODE_BACK) 
				{         // close image gallery         
					return false; // this avoids passing to super     
				}     
				return super.onKeyDown(keyCode, event); 
			} 
			public void back() {     if (history.size() > 0) {         
			history.remove(history.size() - 1);         
			if (history.size() <= 0) {             finish();         } 
			else {             setContentView(history.get(history.size() - 1));
			}     
			} else {         finish();     } }  @Override public void onBackPressed() {     TopNewsGroup.group.back(); }  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {     if (keyCode == KeyEvent.KEYCODE_BACK) {         TopNewsGroup.group.back();         return true;     }     return super.onKeyDown(keyCode, event); } 
			
				*/
			
			// @Override
			// public void onLoadResource(WebView view, String url) {
			// Toast.makeText(getApplicationContext(),
			// "WebViewClient.onLoadResource", Toast.LENGTH_SHORT).show();
			// super.onLoadResource(view, url);
			// }

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {

				Log.d("TAG", "error=" + description);

				// Toast.makeText(getApplicationContext(),
				// errorCode + "/" + description, Toast.LENGTH_LONG).show();
			}

		});

		mWebView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

			public void onCreateContextMenu(ContextMenu menu, final View arg1,
					ContextMenuInfo arg2) {
				// TODO Auto-generated method stub
				MenuItem.OnMenuItemClickListener handler = new MenuItem.OnMenuItemClickListener() {
					String kej_tw1 = "http://www.youtube.com/get_video_info?video_id=";
					String kej_tw2 = "&eurl=http://kej.tw/";
					String r1 = null;
					String r2 = null;
					String YuTu_url = null;
					String YuTu_file;
                    boolean  local_play_flag=false;
                    boolean  share_play_flag=false;
                    int menu_id;
                    
					public boolean onMenuItemClick(MenuItem item) {
						// do the menu action
						switch ( item.getItemId() ) {
						
						case MENU_SHARE:
						case MENU_PLAY:
						case MENU_DOWNLOAD:
							
							menu_id = item.getItemId();
							
							    if( menu_id == MENU_SHARE )
							    {
							    	local_play_flag = false;
							    	share_play_flag = true;
							    	Log.i("share to TV", "TV分享");	
							    }
							    else  if( menu_id == MENU_PLAY )
							    {
							    	local_play_flag = true;
							    	share_play_flag = false;
							    	
									Log.i("you click", "播放");	
							    }
							    else  if( menu_id == MENU_DOWNLOAD )
							    {
							    	local_play_flag = false;
							    	share_play_flag = false;
							    	
							    	Log.i("you click", "下載");
							    }
							
					//============================================================		
							if( web_action == 1 )
							{
								if( !Touchlink_url.isEmpty() )
								{
						     
									 String flvcdurl = "http://www.flvcd.com/parse.php?kw=" + Touchlink_url + "&flag=&format="; 
		                             
							           HttpClient client = new DefaultHttpClient();
							           HttpGet request = new HttpGet( flvcdurl );
							           HttpResponse response1=null;
										try {
											response1 = client.execute(request);
										} catch (ClientProtocolException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										} catch (IOException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
										//String Youku_url = response1.toString();
										HttpEntity yy = response1.getEntity();
										String str1 = null;
										try {
											str1 = EntityUtils.toString(yy);
										//	str1 = "";
										} catch (ParseException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										} catch (IOException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
										
								//==================================================================		
										Pattern pat1 = Pattern.compile("href=\"(.+?)\" target=\"_blank\" class=\"link\"");
								        Matcher m1 = pat1.matcher( str1 );
									        
								            if(m1.find()) { 
								          	   r1 = m1.group(1);
								          	   
								          	    if(  local_play_flag == true )
												{
											               Intent it = new Intent(Intent.ACTION_VIEW);
												     	   Uri uri = Uri.parse(  r1  );
												     	   it.setDataAndType(uri , "video/*");
												     	   startActivity(it);
												}
												else if(  share_play_flag == true )
												{
												
												        new Thread() {  
												            public void run() {  
												               	cs = new ClientSocket( pad_str_ipaddr ,  PORT_NUMBER );  
												            	cs.createConnection();  
												                 if( cs != null )
												                 {
													                   try {
													                	      SendMsg( cs, r1 );
													                	    	
																			} catch (IOException e) {
																				// TODO Auto-generated catch block
																				e.printStackTrace();
																			} 
												              	  }  
												              } //run()  
												         }.start();
												}
												else // 下載
												{
													int i = r1.lastIndexOf("/");
													final String local_r2 = LOCAL_DOWNLOAD_DIR+ r1.substring(i+1);
													 new Thread() {  
												            public void run() { 
												            	DownloadFromUrl( r1, local_r2 );	
												            }  // run 
											         }.start();
												}

								             } //if(m1.find()) 
								          //========================================================
								            else
								            {
								            	 Pattern pat2 = Pattern.compile("href=\"(.+?)\" target=\"_blank\" onclick=");
											     Matcher m2 = pat2.matcher( str1 );
											     if(m2.find()) { 
										          	   r1 = m2.group(1);
										          	   
										          	 if(  local_play_flag == true )
														{
													               Intent it = new Intent(Intent.ACTION_VIEW);
														     	   Uri uri = Uri.parse(  r1  );
														     	   it.setDataAndType(uri , "video/*");
														     	   startActivity(it);
														}
														else if(  share_play_flag == true )
														{
													          	 new Thread() {  
															            public void run() {  
															               	cs = new ClientSocket( pad_str_ipaddr ,  PORT_NUMBER );  
															            	cs.createConnection();  
															                 if( cs != null )
															                 {
																                   try {
																                	      SendMsg( cs, r1 );
																                	    	
																				  	} catch (IOException e) {
																	      			    // TODO Auto-generated catch block
																						e.printStackTrace();
																					} 
															              	  } // if( cs != null )
															              }  // run 
															         }.start();
														}
														else  // 下載
														{
															int i = r1.lastIndexOf("/");
															final String local_r2 = LOCAL_DOWNLOAD_DIR+r1.substring(i+1);
															 new Thread() {  
														            public void run() { 
														            	DownloadFromUrl( r1, local_r2 );	
														            }  // run 
													         }.start();
														}
										          	 
											     } // if(m2.find())
											     else
												 {
													// get the URL of the touched anchor tag
													WebView.HitTestResult hr = ((WebView) arg1)
															.getHitTestResult();
													String str = hr.getExtra(); // check if it is the
																				// URL of the thumbnail
																				// of the video
													int i =  hr.getType();
													switch(hr.getType()) 
													{
													
															case WebView.HitTestResult.SRC_ANCHOR_TYPE:
																str = "";
																break;
															case WebView.HitTestResult.ANCHOR_TYPE:
																str = "";
																break;
															case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
																 String mSelectedUri = null;
																 WebView wv = (WebView)arg1;

																mSelectedUri = wv.getHitTestResult().getExtra();
																String ss = Uri.parse(mSelectedUri).getHost();
																//if(!Uri.parse(mSelectedUri).getHost().equals("habrahabr.ru")) 
																//	return false;

																str = "";
																break;
															case WebView.HitTestResult.IMAGE_ANCHOR_TYPE :
																str = "";
																break;	
															case WebView.HitTestResult.IMAGE_TYPE :
																str = "";
																break;	
													  } // switch(hr.getType()
													} //else
								            	} // else
								        //=======================================================	          
								} // if( !Touchlink_url.isEmpty()
							} // if( web_action == 1 )
							
		//====================== Google YouTube 的鏈結============================================			
							if( web_action == 2 )	
							{
								if( !Touchlink_url.isEmpty() )
								{	
									Pattern pat0 = Pattern.compile("http://m.youtube.com/#/watch\\?v=(.+?)$");
									  Matcher m0 = pat0.matcher( Touchlink_url );
							            if(m0.find()) { 
							          	   r1 = m0.group(1);
							            }
							            else
							            	return false;
							            			
									 String kej_url = kej_tw1 + r1 + kej_tw2; 
		                             
							           HttpClient client = new DefaultHttpClient();
							           HttpGet request = new HttpGet( kej_url );
							           HttpResponse response1=null;
										try {
											response1 = client.execute(request);
										} catch (ClientProtocolException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										} catch (IOException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
										//String Youku_url = response1.toString();
										HttpEntity yy = response1.getEntity();
										String str1 = null;
										try {
											str1 = EntityUtils.toString(yy);
										//	str1 = "";
										} catch (ParseException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										} catch (IOException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
								
										/*   // 取最高畫質的連結
										Pattern pat01 = Pattern.compile("url%3D(.+?)%26");
										  Matcher m01 = pat01.matcher( str1 );
								            if(m01.find()) { 
								          	   r1 = m01.group(1);
								            }
								            else
								            	return false;
								       */
										
										Pattern pat01 = Pattern.compile("url%3D(.+?)%26fallback_host");
										Pattern pat02 = Pattern.compile("(.+?)%26");
										
										  Matcher m01 = pat01.matcher( str1 );
								            if(m01.find()) { 
								          	   r1 = m01.group(1);
								          	   int i;

								          	    i = r1.indexOf("1080", 0);
								          	    	       
								          	    while( i != -1)
								          	   {
								          		 str1 = str1.substring( i+1 );
								          		 m01 = pat01.matcher( str1 );
								          		 if( m01.find()) {
								          			 r1 = m01.group(1);
								          			 i = r1.indexOf("1080", 0);
								         		 }
								          		 else
								          		 {
								          			i = 0; 
								          			r1 = "";
								          		 }
								          	   }
								          	   
								          	    Matcher m03 = pat02.matcher( r1 );
								          	    if(m03.find()) 
								          	     	r2 = m03.group(1);
								            }
								            else
								            	return false;
										
			// 回傳:
			// url%3Dhttp%253A%252F%252Fo-o---preferred---fareastone-khh2---v11---lscache4.c.youtube.com%252Fvideoplayback%253Fupn%253DOnYHz72GcWg%2526sparams%253Dcp%25252Cgcr%25252Cid%25252Cip%25252Cipbits%25252Citag%25252Cratebypass%25252Csource%25252Cupn%25252Cexpire%2526fexp%253D900148%25252C922401%25252C920704%25252C912806%25252C924412%25252C913558%25252C912706%2526key%253Dyt1%2526itag%253D37%2526ipbits%253D8%2526signature%253D6B319167B8EF385CA254F8AB500EE4960EBEEBB7.33954D00E0BE9B8596BC10131AEF8FCDF46D77AA%2526mv%253Dm%2526sver%253D3%2526mt%253D1346721973%2526ratebypass%253Dyes%2526source%253Dyoutube%2526ms%253Dau%2526gcr%253Dtw%2526expire%253D1346747309%2526ip%253D61.20.143.1%2526cp%253DU0hTS1dPVV9FU0NOM19PSVlEOmtqNGFzeWdfWXpE%2526id%253Dda2ce22551894c0d%2526newshard%253Dyes%26quality%3Dhd1080%26fallback_host%3Dtc.v21.cache6.c.youtube.com%26type%3Dvideo%252Fmp4%253B%2Bcodecs%253D%2522avc1.64001F%252C%2Bmp4a.40.2%2522%26itag%3D37%2C
			// 找  "url%3D" 開頭
			// 並 "%26"結束	
			// (example)
			//String rr = "http%253A%252F%252Fo-o---preferred---fareastone-khh2---v11---lscache4.c.youtube.com%252Fvideoplayback%253Fupn%253DOnYHz72GcWg%2526sparams%253Dcp%25252Cgcr%25252Cid%25252Cip%25252Cipbits%25252Citag%25252Cratebypass%25252Csource%25252Cupn%25252Cexpire%2526fexp%253D900148%25252C922401%25252C920704%25252C912806%25252C924412%25252C913558%25252C912706%2526key%253Dyt1%2526itag%253D37%2526ipbits%253D8%2526signature%253D6B319167B8EF385CA254F8AB500EE4960EBEEBB7.33954D00E0BE9B8596BC10131AEF8FCDF46D77AA%2526mv%253Dm%2526sver%253D3%2526mt%253D1346721973%2526ratebypass%253Dyes%2526source%253Dyoutube%2526ms%253Dau%2526gcr%253Dtw%2526expire%253D1346747309%2526ip%253D61.20.143.1%2526cp%253DU0hTS1dPVV9FU0NOM19PSVlEOmtqNGFzeWdfWXpE%2526id%253Dda2ce22551894c0d%2526newshard%253Dyes";
						            //將以上的的 ASCII 解碼成 URL連結
									final String real_url = url25_decode( r2 );
									
								         //   =======   本機試播     =======   
									if(  local_play_flag == true )
									{
								             Intent it = new Intent(Intent.ACTION_VIEW);
									     	   Uri uri = Uri.parse(  real_url  );
									     	   it.setDataAndType(uri , "video/*");
									     	   startActivity(it);
									}
									else if(  share_play_flag == true )
									{
								          	 //========================================================
										        new Thread() {  
										            public void run() {  
										               	cs = new ClientSocket( pad_str_ipaddr ,  PORT_NUMBER );  
										            	cs.createConnection();  
										                 if( cs != null )
										                 {
											                   try {
											                	      SendMsg( cs, real_url );
											                	    	
																	} catch (IOException e) {
																		// TODO Auto-generated catch block
																		e.printStackTrace();
																	} 
										              	  }  
										              } //run()  
										         }.start();
								     
									} 
									else  // 下載
									{
										int i = real_url.lastIndexOf("/");
										final String local_r2 = LOCAL_DOWNLOAD_DIR+real_url.substring(i+1);
										 new Thread() {  
									            public void run() { 
									            	DownloadFromUrl( real_url, local_r2 );	
									            }  // run 
								         }.start();
									}     
						
								} // if( !Touchlink_url.isEmpty(
								else
								{
								// get the URL of the touched anchor tag
								WebView.HitTestResult hr = ((WebView) arg1)
										.getHitTestResult();
								String str = hr.getExtra(); // check if it is the
															// URL of the thumbnail
															// of the video
								int i =  hr.getType();
									switch(hr.getType()) {
									
											case WebView.HitTestResult.SRC_ANCHOR_TYPE:
												str = "";
												break;
											case WebView.HitTestResult.ANCHOR_TYPE:
												str = "";
												break;
											case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
												 String mSelectedUri = null;
												 WebView wv = (WebView)arg1;
		
												mSelectedUri = wv.getHitTestResult().getExtra();
												String ss = Uri.parse(mSelectedUri).getHost();
												//if(!Uri.parse(mSelectedUri).getHost().equals("habrahabr.ru")) 
												//	return false;
		
												str = "";
												break;
											case WebView.HitTestResult.IMAGE_ANCHOR_TYPE :
												str = "";
												break;	
											case WebView.HitTestResult.IMAGE_TYPE :
												str = "";
												break;	
									} // switch
								} // else
								
							} // if( web_action == 2 )
	//======================以上為  Google YouTube 的鏈結====================================
						
						return false;

						
						//	break;
						//case MENU_DOWNLOAD:
						//	Log.i("you click", "下載");
						//	break;
						case MENU_LINKTO:
							Log.i("you click", "進入連結");
							break;
						} // switch (item.getItemId()) 
						return true;
					} // onMenuItemClick
				};
				Log.i("long click", "true");

				HitTestResult result = ((WebView) arg1).getHitTestResult();
				int resultType = result.getType();
				
				
             
				if( web_action == 2 )
				{
					//web_action = 1;
					Touchlink_url = result.getExtra();
				}
			
				
				if ((resultType == HitTestResult.ANCHOR_TYPE)
						|| (resultType == HitTestResult.IMAGE_ANCHOR_TYPE)
						|| (resultType == HitTestResult.SRC_ANCHOR_TYPE)
						|| (resultType == HitTestResult.SRC_IMAGE_ANCHOR_TYPE)) {
					Intent i = new Intent();
					Log.i("extrea", result.getExtra());
					MenuItem item2 = menu.add(0, MENU_SHARE, 0, "分享到TV電視")
							.setOnMenuItemClickListener(handler);
					item2.setIntent(i);
					item2 = menu.add(0, MENU_PLAY, 0, "本機立即播放")
							.setOnMenuItemClickListener(handler);
					item2.setIntent(i);

					item2 = menu.add(0, MENU_DOWNLOAD, 0, "下載")
							.setOnMenuItemClickListener(handler);
					;
					item2.setIntent(i);

					item2 = menu.add(0, MENU_LINKTO, 0, "進入連結")
							.setOnMenuItemClickListener(handler);
					;
					item2.setIntent(i);
					menu.setHeaderTitle("選擇您需要的功能");          //result.getExtra());
				} else if (resultType == HitTestResult.IMAGE_TYPE) {
					
					Toast.makeText(getApplicationContext(),
					        			"Can not play this movie!", Toast.LENGTH_SHORT).show();
				/*	
					Log.i("image type", "ture");
					Intent i = new Intent();
					MenuItem item = menu.add(0, 1, 0, "Share to TV").setOnMenuItemClickListener(handler);
					item.setIntent(i);
					item = menu.add(0, 2, 0, "Save").setOnMenuItemClickListener(handler);
					item.setIntent(i);
					menu.setHeaderTitle("選擇您需要的功能");    //result.getExtra());
				 */
					
				}
			} // public void onCreateContextMenu
		}); // mWebView.setOnCreateContextMenuListener

		
	//	mWebView.loadUrl("http://m.youtube.com/results?q="+keyword);
	//  test purpose //	mWebView.loadUrl("http://m.youtube.com");
	//	mWebView.loadUrl("http://www.youku.com/");
		if( web_action == 1  )
		{
			if( keyword == "" )
				mWebView.loadUrl("http://u.youku.com/magic-pad");
			else
				mWebView.loadUrl("http://www.soku.com/search_video/q_"+keyword );
		}
		else if( web_action == 2  ) 
		{
			mWebView.loadUrl("http://m.youtube.com/results?q="+keyword);
		}
		
		 //  ==============  找 圖片  ==============
	     //  mWebView.loadUrl("http://image.baidu.com/i?ct=201326592&cl=2&nc=1&lm=-1&st=-1&tn=baiduimage&istype=2&fm=index&pv=&z=0&word="+keyword);
		 //  =====================================
	} // OnCreate

	// ================================================================
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Toast.makeText(getApplicationContext(),
		// newurl, Toast.LENGTH_LONG).show();

	//	getMenuInflater().inflate(R.menu.activity_main, menu);
		
		super.onCreateOptionsMenu(menu); 
		/** Add one menu item for each View in our project */ 
		menu.add(0, 0, 0, "STOP TV play (停止TV播放)"); 
		menu.add(0, 1, 0, "Pause/Play");
		menu.add(0, 2, 0, "Search TV"); 
		return true; 

		
	}
	
	@Override 
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int order = item.getOrder(); 
		int id = item.getItemId();
  		switch (id) { 
		case 0: 
		 	cs = new ClientSocket( pad_str_ipaddr ,  PORT_NUMBER );  
        	cs.createConnection();  
             if( cs != null )
             {
                   try {
                	      SendMsg( cs, "#" );
                	    	
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
          	  }  
		return true; 
		case 1: 
			cs = new ClientSocket( pad_str_ipaddr ,  PORT_NUMBER );  
        	cs.createConnection();  
             if( cs != null )
             {
                   try {
                	      SendMsg( cs, "!" );
                	    	
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
          	  }  
		      return true; 
		case 2: 
				onBroadcast_Findclient();
					
			return true;
		} 
 		return true;
		
	}

	// ================================================================
	public String get_real_youtube_filelink(String url_link) {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url_link);

		// HttpClient client = new DefaultHttpClient();
		// HttpGet request = new HttpGet( url );

		HttpResponse response = null;
		try {
			response = client.execute(request);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String retSrc = null;
		try {
			retSrc = EntityUtils.toString(response.getEntity());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Pattern p = Pattern
				.compile("&url_encoded_fmt_stream_map=url%3D(.+?)%26quality");
		Matcher m = p.matcher(retSrc);
		String r = null;
		while (m.find()) {
			r = m.group(1);
		}
		if (r == null)
			return "";

		// converter rule:
		String s0 = "";
		String rr, h, hw;
		int p0, p1, i;
		p0 = 0;
		rr = r;
		i = 0;
		int count = 0;

		// 以下將 %25 和隨後的16進位數字碼, 轉換成文字
		while ((p1 = rr.indexOf("%25", 0)) != 0) {
			count++;
			Log.d("", "" + count);
			if (p1 == -1) {
				s0 = s0 + rr.substring(i, rr.length());
				break;
			}
			s0 = s0 + rr.substring(i, p1);
			h = rr.substring(p1 + 3, p1 + 5);
			hw = Hex_decode(h);
			s0 = s0 + hw;
			p0 = p1 + 5;
			if (rr.length() <= 0)
				break;
			rr = rr.substring(p0 - 1, rr.length());
			i = 1;
			if (rr.length() <= 0)
				break;
		}
		return s0;
	} // public boolean get_real_youtube_filelink
	
	// ================================================================
	/*
	*     將 %25 的組成的碼轉成 URL
	*/
	public static String url25_decode( String rr ) 
	{
		// converter rule:
				String s0 = "";
				String  h, hw;
				int p0, p1, i;
				p0 = 0;
			//	rr = r;
				i = 0;
				int count = 0;

				// 以下將 %25 和隨後的16進位數字碼, 轉換成文字
				while ((p1 = rr.indexOf("%25", 0)) != 0) {
					count++;
					Log.d("", "" + count);
					if (p1 == -1) {
						s0 = s0 + rr.substring(i, rr.length());
						break;
					}
					s0 = s0 + rr.substring(i, p1);
					h = rr.substring(p1 + 3, p1 + 5);
					hw = Hex_decode(h);
					s0 = s0 + hw;
					p0 = p1 + 5;
					if (rr.length() <= 0)
						break;
					rr = rr.substring(p0 - 1, rr.length());
					i = 1;
					if (rr.length() <= 0)
						break;
				}
				
		return s0;
	}
	// ================================================================
	/*
	 * 將16進制數位解碼成字串,適用於所有字元（包括中文）
	 */
	private static String hexString = "0123456789ABCDEF";
	public static String Hex_decode(String bytes) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(
				bytes.length() / 2);
		// 將每2位元16進制整數組裝成一個位元組
		for (int i = 0; i < bytes.length(); i += 2)
			baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
					.indexOf(bytes.charAt(i + 1))));
		return new String(baos.toByteArray());
	}
	// ================================================================
	private void SendMsg(ClientSocket socket,String msg) throws IOException
    {
		DataOutputStream dos = null;
        dos = cs.getOutputStream();
     
        if ( dos == null)  
	           return;
        
        dos.writeUTF( msg );  
     //   dos.flush(); 
        dos.flush(); 
        dos.close();  
    }
	
	//---------------------------------------------------------------------------------------
    public static boolean Port_Scan( final int port_num )
    {
    	byte[] liveip;
    	String thisip_h;
    	Thread[] socketThread = new Thread[255];
    	   	
    	 String thisip = getLocalIpAddress();
    	for( int i = 1; i <= 254; i++) 
    	{
    		String thisip_p = thisip.substring( 0, thisip.lastIndexOf('.')+1); 
    		thisip_x[i] = thisip_p + i;
    	}
    	
    	found_count = 0;
    	
    	for( int  j = 1; j <= 254; j++) 
    	{
    	//	ti[ipn]=ipn;
    	//	String fullip = thisip_1 + ipn;
    	//	final String fullip = thisip_x[j];
    		final int ip_n = j;
    		//socketThread[j] = new Thread( new Runnable()
    		 new Thread() {  
    			 
    	 //         public void run() //{	
		//	{
    			 //int ipaddr = ipn;
    		//	int[] x= new int[1];
    		// = j;
		        @Override
		        public void run() {
		        	
		        	chk_socket( ip_n , port_num ); //fullip );
 		//	String thisip_1 = thisip.substring( 0, thisip.lastIndexOf('.')+1); //.toString(); 
		        	
		        }
			 }.start();
			//});
    	}
    		
    	if( found_count == 0)
    		return false;
    	else
    		return true;
   
    }  //Port_Scan( )
  //---------------------------------------------------------------------------------------
    static void chk_socket ( int ip_n , int port_num ) // String sock_ip)
    {
    	final String sock_ip = thisip_x[ip_n];
    	Socket socket2 = null;
		try {
			socket2 = new Socket( sock_ip , port_num );
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			return;
		} 
		
		try {
			server_found_ip[found_count++] = sock_ip;
			
			socket2.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		}   
     }  //chk_socket ()
  //---------------------------------------------------------------------------------------
    public static String getLocalIpAddress() { 
        try { 
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) { 
                NetworkInterface intf = en.nextElement(); 
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) { 
                    InetAddress inetAddress = enumIpAddr.nextElement(); 
                    if (!inetAddress.isLoopbackAddress()) { 
                        return inetAddress.getHostAddress().toString(); 
                    } 
                } 
            } 
        } catch (SocketException ex) { 
           // Log.e(LOG_TAG, ex.toString()); 
        } 
        return null; 
    }  //  getLocalIpAddress() 
    
  //---------------------------------------------------------------------------------------
  //=================== Get  server information  =================
  	private String Get_Host_IP(String filename) 
  	{
  			String result = null;
  			NodeList nod_config        = null; 
  			int iElementLength = 0; 
  		 try{
  				File file = new File( filename );
  				if( !file.exists() )
  				{
  					String st = String.format( "Couldn't find XML file..." );
  					return st;
  				}
  				
  				builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();     
  				document = builder.parse(file); 
  				document.getDocumentElement().normalize();
  				
  			//	upload_sum = document.getElementsByTagName("upload").getLength();
  				
  			//	Toast.makeText(this, " 開始進行資料備份, 請稍待...", 0).show();
  			//	upload_serial = 0;
  				
  				nod = 	document.getElementsByTagName("IP_Address"); 
  			    if( nod.getLength()>0 )
  			    {
  			         	ele_1 =  nod.item( 0 ); 
  			         	//str_ipaddr = ele_1.getTextContent();   //Android 2.3
  			         	str_ipaddr = ele_1.getFirstChild().getNodeValue(); //Android 1.5
  			     }
  			    
  			  nod = 	document.getElementsByTagName("PC_IP_Address"); 
			    if( nod.getLength()>0 )
			    {
			         	ele_1 =  nod.item( 0 ); 
			         	//str_ipaddr = ele_1.getTextContent();   //Android 2.3
			         	pc_str_ipaddr = ele_1.getFirstChild().getNodeValue(); //Android 1.5
			    }
			    
			    nod = 	document.getElementsByTagName("PAD_IP_Address"); 
  			    if( nod.getLength()>0 )
  			    {
  			         	ele_1 =  nod.item( 0 ); 
  			         	//str_ipaddr = ele_1.getTextContent();   //Android 2.3
  			         	pad_str_ipaddr = ele_1.getFirstChild().getNodeValue(); //Android 1.5
  			    }
  		 }
  		 catch( Exception e ){  // try ... catch
  				e.printStackTrace();
  				Log.d("main",String.valueOf(e.toString())); 
  		 } 
  		 
  		 return "";
  	}
  //---------------------------------------------------------------------------------------
   
    public String readFileSdcard(String fileName){
        String res=""; 
        try{ 
         FileInputStream fin = new FileInputStream(fileName); 
         int length = fin.available(); 
         byte [] buffer = new byte[length]; 
         fin.read(buffer);     
         res = EncodingUtils.getString(buffer, "UTF-8"); 
         
         int i = res.indexOf("\n");
         if( i != -1)
         res = res.substring( 0, i);
         
         fin.close();     
        } 
        catch(Exception e){ 
         e.printStackTrace(); 
        } 
        return res; 
   }
  //-----------------------------------------------------------
    

    public void DownloadFromUrl(String imageURL, String fileName) 
    {  //this is the downloader method
            try {
          	//you can write here any link
                    URL url = new URL( imageURL ); 
                    File file = new File(fileName);

                    long startTime = System.currentTimeMillis();
                    Log.d("ImageManager", "download begining");
                    Log.d("ImageManager", "download url:" + url);
                    Log.d("ImageManager", "downloaded file name:" + fileName);
                    /* Open a connection to that URL. */
                    URLConnection ucon = url.openConnection();

                    /*
                     * Define InputStreams to read from the URLConnection.
                     */
                    InputStream is = ucon.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is);

                    /*
                     * Read bytes to the Buffer until there is nothing more to read(-1).
                     */
                    ByteArrayBuffer baf = new ByteArrayBuffer(50);
                    int current = 0;
                    while ((current = bis.read()) != -1) {
                            baf.append((byte) current);
                    }

                    /* Convert the Bytes read to a String. */
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(baf.toByteArray());
                    fos.close();
                    Log.d("ImageManager", "download ready in"
                                    + ((System.currentTimeMillis() - startTime) / 1000)
                                    + " sec");

            } catch (IOException e) {
                    Log.d("ImageManager", "Error: " + e);
            }

    }
  //===================================================================  
  // 　UDP 廣播封包, 要找 client 端
    private void onBroadcast_Findclient() {

		new Thread() {
			
			String value;
			String inetaddress;
			
			public void run() {
				try {
					socket = new DatagramSocket( BROADCAST_PORT );
					socket.setBroadcast(true);
					
					//byte[] data = "Hello! This is broadcast server.".getBytes("UTF-8");
					byte[] data = KEYWORD_SEARCH.getBytes("UTF-8");
					packet = new DatagramPacket(data, data.length, getBroadcastAddress(), BROADCAST_PORT );
					
		  			socket.send(packet);
		  		/*	handler.post(new Runnable() {
		  				public void run() {
				  			txt1.setText("Local: " + MyIP + ":" + packet.getPort());
		  				}
		  			});
		  		*/	
		  			//socket.close();
		  			
		  			//-----------  接收Client回傳的程序 --------------
		  			//socket = new DatagramSocket(1818);
					//socket.setBroadcast(true);
		  			byte[] buf = new byte[1024];
		  			
               while( true )
               {
					
		  			DatagramPacket packet = new DatagramPacket(buf, buf.length);
					socket.receive(packet);
					
					if( packet.getLength() > 0 )
					{
						buf[packet.getLength()] = 0;
						value = new String(buf);
					    buf[0]=0;
					    
					    
					    if( value.indexOf( KEYWORD_IAMHERE ) == 0 )  //判斷是否收到者的回應:"I am Here"
						{
					    	inetaddress = (packet.getAddress()).getHostAddress() ;

								handler.post(new Runnable() {
									public void run() {
					
										Toast.makeText(getBaseContext(),"Find TV: "+inetaddress,
														Toast.LENGTH_SHORT).show();	
												
										 writeFileSdcard( SERVERIP_SETTINGFILE , inetaddress );
										
									}
								});
								socket.close();
						}
					}
					
               }
               
				//	socket.close();

				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
					socket.close();
				}
				
			}
		}.start();
	}
  //===================================================================
    InetAddress getBroadcastAddress() throws IOException {
  		WifiManager wifi = (WifiManager)getSystemService(WIFI_SERVICE);
  		DhcpInfo dhcp = wifi.getDhcpInfo();

  		MyIP = String.format("%d.%d.%d.%d", (dhcp.ipAddress & 0xff), (dhcp.ipAddress >> 8 & 0xff), (dhcp.ipAddress >> 16 & 0xff), (dhcp.ipAddress >> 24 & 0xff));
  		// handle null somehow    
  		int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
  		byte[] quads = new byte[4];
  		for (int k = 0; k < 4; k++)
  			quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
  		return InetAddress.getByAddress(quads);
  	}
  //===================================================================
  
    public void writeFileSdcard(String fileName,String message){ 
        try{ 
         //FileOutputStream fout = openFileOutput(fileName, MODE_PRIVATE);
        FileOutputStream fout = new FileOutputStream(fileName);
         byte [] bytes = message.getBytes(); 
         fout.write(bytes); 
          fout.close(); 
         } 
        catch(Exception e){ 
         e.printStackTrace(); 
        } 
    }
    
} // public class MainActivity

/*
 * mWebView.setWebChromeClient( new WebChromeClient() { public void
 * onProgressChanged(WebView view, int progress) {
 * //activity.setTitle("Loading..."); //activity.setProgress(progress * 100);
 * //if(progress == 100) // activity.setTitle(R.string.app_name); }
 * 
 * public void onReceivedError(WebView view, int errorCode, String description,
 * String failingUrl) { // Handle the error } // @Override public boolean
 * shouldOverrideUrlLoading(WebView view, String url) { view.loadUrl(url);
 * return true; }
 * 
 * });
 * 
 * 
 * 
 * 
 * 
 * b1.setOnClickListener(new View.OnClickListener() {
 * 
 * 
 * 
 * // @Override
 * 
 * public void onClick(View v) {
 * 
 * 
 * mWebView.loadUrl(et.getText().toString());
 * 
 * }
 * 
 * });
 */

/*
 * // 另存 ( save Link ) code reference
 * 
 * HitTestResult result = getHitTestResult(); HttpClient httpclient = new
 * DefaultHttpClient(); HttpGet httpget = new HttpGet(result.getExtra());
 * HttpResponse response = httpclient.execute(httpget); HttpEntity entity =
 * response.getEntity(); if (entity != null) { URL url = new
 * URL(result.getExtra());
 * 
 * //Grabs the file part of the URL string String fileName = url.getFile();
 * 
 * //Make sure we are grabbing just the filename int index =
 * fileName.lastIndexOf("/"); if(index >= 0) fileName =
 * fileName.substring(index);
 * 
 * //Create a temporary file File tempFile = new
 * File(Environment.getExternalStorageDirectory(), fileName);
 * if(!tempFile.exists()) tempFile.createNewFile();
 * 
 * InputStream instream = entity.getContent(); BufferedInputStream
 * bufferedInputStream = new BufferedInputStream(inputStream); //Read bytes into
 * the buffer ByteArrayBuffer buffer = new ByteArrayBuffer(50); int current = 0;
 * while ((current = bufferedInputStream.read()) != -1) { buffer.append((byte)
 * current); }
 * 
 * //Write the buffer to the file FileOutputStream stream = new
 * FileOutputStream(tempFile); stream.write(buffer.toByteArray());
 * stream.close(); } }
 * 
 * //try{   Thread.sleep(1000);} catch (InterruptedException exc) {   }
 * 
 */
