package com.clickntap.tool.social;

import java.net.URLEncoder;

import com.clickntap.utils.ConstUtils;
import com.clickntap.utils.HTMLUtils;

public class SocialScore {

	public static void main(String args[]) throws Exception {
		SocialScore score = new SocialScore("Affari tuoi", "");
		System.out.println(score.getNumberOfResults());
		System.out.println(score.getNumberOfLikes());
	}

	private int numberOfResults;
	private int numberOfLikes;

	public SocialScore(String query, String site) {
		try {
			numberOfResults = 0;
			numberOfLikes = 0;
			query = URLEncoder.encode(query + " site:" + site, ConstUtils.UTF_8);
			String html = HTMLUtils.getSource("https://www.google.it/search?q=" + query + "&tbs=qdr:w");
			html = html.replace(".", "");
			html = html.replace(" ", "");
			html = html.toLowerCase();
			numberOfResults = getScore("id=\"resultstats\"", html);
			numberOfLikes = getScore("piacea", html) + getScoreReverse("likes", html);
			System.out.println("socialscore(" + query + "," + site + "):" + numberOfResults + "|" + numberOfLikes);
		} catch (Throwable t) {
		}
	}

	private int getScore(String channel, String html) {
		int score = 0;
		int x = 0;
		while ((x = html.indexOf(channel, x)) > 0) {
			if (x > 0) {
				int x0 = x;
				while (!Character.isDigit(html.charAt(x0))) {
					x0++;
				}
				int x1 = x0;
				while (Character.isDigit(html.charAt(x1))) {
					x1++;
				}
				try {
					score += Integer.parseInt(html.substring(x0 + 1, x1));
				} catch (NumberFormatException e) {
				}
				x = x1;
			}
		}
		return score;
	}

	private int getScoreReverse(String channel, String html) {
		int score = 0;
		int x = 0;
		while ((x = html.indexOf(channel, x)) > 0) {
			if (x > 0) {
				int x1 = x;
				while (!Character.isDigit(html.charAt(x1))) {
					x1--;
				}
				int x0 = x1 - 1;
				while (Character.isDigit(html.charAt(x0))) {
					x0--;
				}
				if (html.charAt(x0) == '>') {
					try {
						score += Integer.parseInt(html.substring(x0 + 1, x1));
					} catch (NumberFormatException e) {
					}
				}
				x += channel.length();
			}
		}
		return score;
	}

	public int getNumberOfResults() {
		return numberOfResults;
	}

	public int getNumberOfLikes() {
		return numberOfLikes;
	}

}
