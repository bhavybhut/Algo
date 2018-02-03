package com.multimedia.algo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;

public class Main {

	/**
	 * @param args
	 */
	public static int temp = 0;
	public static String ContenetType = null;
	public static String WHword = null;
	public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	public static final String DB_URL = "jdbc:mysql://localhost/multimedia";

	public static final String USER = "";
	public static final String PASS = "";

	public static void main(String[] args) throws Exception {
		System.out
				.println("please Enter Question . (Example : what is multimedia)");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String que = "";
		try {
			que = br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("question is : " + que);
		// find out wh word from it
		Pattern p = Pattern.compile("[WwHh][HhOo][a-zA-Z]*");
		Matcher m = p.matcher(que);
		while (m.find()) {
			System.out.println("Wh word is : " + m.group());
			WHword = m.group();
		}
		String[] words = que.split(" ");
		String queryString = "";

		for (int i = 0; i < words.length; i++) {
			if (!(words[i].equals("is") || words[i].equals("are")
					|| words[i].equals("was") || words[i].equals("were") || words[i]
						.equals("will"))) {
				queryString += words[i] + " + ";

			}
		}
		System.out.println("query string to serach on search engine is : "
				+ queryString);
		System.out.println(WHword);

		if (WHword == null) {
			ContenetType = "text";
		}

		if (WHword != null)
			if (WHword.equalsIgnoreCase("how")) {
				ContenetType = "video";
			} else if (WHword.equalsIgnoreCase("what")) {
				ContenetType = "image";
			} else if (WHword.equalsIgnoreCase("who")) {
				ContenetType = "image";
			}
		// this string will be search on different searchengines to get ralted
		// data.

		StringBuilder builder = new StringBuilder();
		int client = temp % 5 + 1;

		String url = "http://clients" + client
				+ ".google.com/complete/search?hl=en&q=" + queryString
				+ "&output=toolbar";
		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();

		DocumentBuilder b;
		try {
			b = f.newDocumentBuilder();
			Document doc = b.parse(url);
			doc.getDocumentElement().normalize();

			NodeList items = doc.getElementsByTagName("CompleteSuggestion");
			for (int i = 0; i < items.getLength(); i++) {
				Node n = items.item(i);
				if (n.getNodeType() == 1) {
					Element e = (Element) n;

					NodeList titleList = e.getElementsByTagName("suggestion");

					Element titleElem = (Element) titleList.item(0);

					builder.append(titleElem.getAttribute("data"));
					builder.append(",");
					builder.append("\n");
				}
			}
			// System.out.println(builder.toString());

			if (ContenetType.equalsIgnoreCase("image")) {
				fetchimage(queryString);
			} else if (ContenetType.equalsIgnoreCase("video")) {
				fetchvideo(queryString);
			} else if (ContenetType.equalsIgnoreCase("text")) {
				JOptionPane.showMessageDialog(null, builder.toString());
			}
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void fetchimage(String queryString) {
		try {
			URL url2 = new URL(
					"https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q="
							+ URLEncoder.encode(queryString.replace("+", "")));
			URLConnection connection = url2.openConnection();

			String line;
			StringBuilder builder2 = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			while ((line = reader.readLine()) != null) {
				builder2.append(line);
			}

			JSONObject json = new JSONObject(builder2.toString());
			JFrame.setDefaultLookAndFeelDecorated(true);
			JFrame frame = new JFrame("Multimedia Algo Image Search");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(new GridLayout(2, 2));
			for (int i = 0; i < 4; i++) {
				BufferedImage resizedImage = resize(
						ImageIO.read(new URL(json.getJSONObject("responseData")
								.getJSONArray("results").getJSONObject(i)
								.getString("url"))), 400, 400);
				frame.add(new JButton(new ImageIcon(resizedImage)));
			}
			frame.pack();
			frame.setVisible(true);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Failure",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	public static BufferedImage resize(BufferedImage image, int width,
			int height) {
		BufferedImage bi = new BufferedImage(width, height,
				BufferedImage.TRANSLUCENT);
		Graphics2D g2d = (Graphics2D) bi.createGraphics();
		g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY));
		g2d.drawImage(image, 0, 0, width, height, null);
		g2d.dispose();
		return bi;
	}

	private static void fetchvideo(String textQuery) throws Exception {
		String clientID = "MultimediaAlgo";

		int maxResults = 2;

		boolean filter = true;

		int timeout = 2000;

		YouTubeManager ym = new YouTubeManager(clientID);

		List<YouTubeVideo> videos = ym.retrieveVideos(textQuery, maxResults,
				filter, timeout);

		for (YouTubeVideo youtubeVideo : videos) {

			System.out.println(youtubeVideo.getWebPlayerUrl());

			System.out.println("Thumbnails");

			for (String thumbnail : youtubeVideo.getThumbnails()) {

				System.out.println("t" + thumbnail);

			}

			System.out.println(youtubeVideo.getEmbeddedWebPlayerUrl());

			System.out.println("************************************");

			String url = "" + youtubeVideo.getEmbeddedWebPlayerUrl();

			if (Desktop.isDesktopSupported()) {
				Desktop desktop = Desktop.getDesktop();
				try {
					desktop.browse(new URI(url));
				} catch (IOException | URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				Runtime runtime = Runtime.getRuntime();
				try {
					runtime.exec("xdg-open " + url);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}
}
