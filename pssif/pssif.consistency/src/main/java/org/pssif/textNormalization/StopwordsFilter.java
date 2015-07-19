package org.pssif.textNormalization;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.pssif.consistencyDataStructures.Token;

/**
This file is part of PSSIF Consistency. It is responsible for keeping consistency between different requirements models or versions of models.
Copyright (C) 2014 Andreas Genz

PSSIF Consistency is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

PSSIF Consistency is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with PSSIF Consistency.  If not, see <http://www.gnu.org/licenses/>.

Feel free to contact me via eMail: genz@in.tum.de
*/

/**
 * THis class implements a normalization step used in the normalization class.
 * It takes a list of tokens and deletes the germand and/or english stopwords
 * from it.
 * 
 * @author Andreas
 * 
 */
public class StopwordsFilter {

	private final StopWordList stopwordListGerman, stopwordListEnglish;

	public StopwordsFilter() {
		stopwordListGerman = new StopWordListGerman();
		stopwordListEnglish = new StopWordListEnglish();
	}

	/**
	 * This method get's a list of tokens and deletes german and/or english
	 * stopwords from it.
	 * 
	 * @param tokens
	 *            The list of tokens where stopwords should be filtered.
	 * @param filterGermanStopWords
	 *            A bool saying whether german stopwords should be filtered
	 * @param filterEnglishStopWords
	 *            A bool saying whether english stopwords should be filtered
	 * @return The list without the stopwords.
	 */
	public List<Token> filterStopWords(List<Token> tokens,
			boolean filterGermanStopWords, boolean filterEnglishStopWords) {
		if (filterGermanStopWords) {
			tokens = doWork(tokens, stopwordListGerman);
		}
		if (filterEnglishStopWords) {
			tokens = doWork(tokens, stopwordListEnglish);
		}

		return tokens;
	}

	/**
	 * modified by Andreas Genz
	 * 
	 * Source: StopWordList StopWordlistGerman StopWordListEnglish
	 * GermanStopWordFilterOperator EnglishStopWordFilterOperator
	 * 
	 * from folder: Text Processing (Tokenizing, Stemming, StopWords)
	 * 
	 * @author RapidMiner
	 * 
	 */

	/*
	 * RapidMiner Text Processing Extension
	 * 
	 * Copyright (C) 2001-2013 by Rapid-I and the contributors
	 * 
	 * Complete list of developers available at our web site:
	 * 
	 * http://rapid-i.com
	 * 
	 * This program is free software: you can redistribute it and/or modify it
	 * under the terms of the GNU Affero General Public License as published by
	 * the Free Software Foundation, either version 3 of the License, or (at
	 * your option) any later version.
	 * 
	 * This program is distributed in the hope that it will be useful, but
	 * WITHOUT ANY WARRANTY; without even the implied warranty of
	 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero
	 * General Public License for more details.
	 * 
	 * You should have received a copy of the GNU Affero General Public License
	 * along with this program. If not, see http://www.gnu.org/licenses/.
	 */
	/**
	 * @author Sebastian Land
	 * 
	 */
	private interface StopWordList {
		/**
		 * Indicates if this word is a stopword
		 */
		public boolean isStopword(String str);
	}

	/**
	 * Stopwords for the German language
	 * 
	 * @author Sebastian Land
	 */
	private class StopWordListGerman implements StopWordList {

		public final String[] STOP_WORDS_SENTIMENT = new String[] { "ab",
				"bei", "da", "deshalb", "ein", "für", "haben", "hier", "ich",
				"ja", "kann", "machen", "muesste", "nach", "oder", "seid",
				"sonst", "und", "vom", "wann", "wenn", "wie", "zu", "bin",
				"eines", "hat", "manche", "solches", "an", "anderm", "bis",
				"das", "deinem", "demselben", "dir", "doch", "einig", "er",
				"eurer", "hatte", "ihnen", "ihre", "ins", "jenen", "keinen",
				"manchem", "meinen", "seine", "soll", "unserm", "welche",
				"werden", "wollte", "während", "alle", "allem", "allen",
				"aller", "alles", "als", "also", "am", "ander", "andere",
				"anderem", "anderen", "anderer", "anderes", "andern", "anderr",
				"anders", "auch", "auf", "aus", "bist", "bsp.", "daher",
				"damit", "dann", "dasselbe", "dazu", "daß", "dein", "deine",
				"deinen", "deiner", "deines", "dem", "den", "denn",
				"denselben", "der", "derer", "derselbe", "derselben", "des",
				"desselben", "dessen", "dich", "die", "dies", "diese",
				"dieselbe", "dieselben", "diesem", "diesen", "dieser",
				"dieses", "dort", "du", "durch", "eine", "einem", "einen",
				"einer", "einige", "einigem", "einigen", "einiger", "einiges",
				"einmal", "es", "etwas", "euch", "euer", "eure", "eurem",
				"euren", "eures", "ganz", "ganze", "ganzen", "ganzer",
				"ganzes", "gegen", "gemacht", "gesagt", "gesehen", "gewesen",
				"gewollt", "hab", "habe", "hatten", "hin", "hinter", "ihm",
				"ihn", "ihr", "ihrem", "ihren", "ihrer", "ihres", "im", "in",
				"indem", "ist", "jede", "jedem", "jeden", "jeder", "jedes",
				"jene", "jenem", "jener", "jenes", "jetzt", "kein", "keine",
				"keinem", "keiner", "keines", "konnte", "können", "könnte",
				"mache", "machst", "macht", "machte", "machten", "man",
				"manchen", "mancher", "manches", "mein", "meine", "meinem",
				"meiner", "meines", "mich", "mir", "mit", "muss", "musste",
				"müßt", "noch", "nun", "nur", "ob", "ohne", "sage", "sagen",
				"sagt", "sagte", "sagten", "sagtest", "sehe", "sehen", "sehr",
				"seht", "sein", "seinem", "seinen", "seiner", "seines",
				"selbst", "sich", "sicher", "sie", "sind", "so", "solche",
				"solchem", "solchen", "solcher", "sollte", "sondern", "um",
				"uns", "unse", "unsen", "unser", "unses", "unter", "von",
				"vor", "war", "waren", "warst", "was", "weg", "weil", "weiter",
				"welchem", "welchen", "welcher", "welches", "werde", "wieder",
				"will", "wir", "wird", "wirst", "wo", "wolle", "wollen",
				"wollt", "wollten", "wolltest", "wolltet", "würde", "würden",
				"z.B.", "zum", "zur", "zwar", "zwischen", "über", "aber",
				"abgerufen", "abgerufene", "abgerufener", "abgerufenes",
				"acht", "acute", "allerdings", "allerlei", "allg", "allgemein",
				"amp", "and", "andererseits", "andernfalls", "anerkannt",
				"anerkannte", "anerkannter", "anerkanntes", "anfangen",
				"anfing", "angefangen", "angesetze", "angesetzt",
				"angesetzten", "angesetzter", "ansetzen", "anstatt",
				"arbeiten", "aufgehört", "aufgrund", "aufhören", "aufhörte",
				"aufzusuchen", "ausdrücken", "ausdrückt", "ausdrückte",
				"ausgenommen", "ausser", "ausserdem", "author", "autor",
				"außen", "außer", "außerdem", "außerhalb", "background",
				"bald", "bearbeite", "bearbeiten", "bearbeitete",
				"bearbeiteten", "bedarf", "bedurfte", "bedürfen", "been",
				"befragen", "befragte", "befragten", "befragter", "begann",
				"beginnen", "begonnen", "behalten", "behielt", "beide",
				"beiden", "beiderlei", "beides", "beim", "beinahe",
				"beitragen", "beitrugen", "bereits", "berichten", "berichtet",
				"berichtete", "berichteten", "bestehen", "besteht", "bevor",
				"bezüglich", "bietet", "bisher", "bislang", "biz", "bleiben",
				"blieb", "bloss", "bloß", "border", "brachte", "brachten",
				"brauchen", "braucht", "bringen", "bräuchte", "bzw", "böden",
				"ca", "ca.", "com", "comment", "content", "dabei", "dadurch",
				"dafür", "dagegen", "dahin", "damals", "danach", "daneben",
				"dank", "danke", "danken", "dannen", "daran", "darauf",
				"daraus", "darf", "darfst", "darin", "darum", "darunter",
				"darüber", "darüberhinaus", "dass", "davon", "davor",
				"demnach", "denen", "dennoch", "derart", "derartig", "derem",
				"deren", "derjenige", "derjenigen", "derzeit", "desto",
				"deswegen", "diejenige", "diesseits", "dinge", "direkt",
				"direkte", "direkten", "direkter", "doc", "doppelt", "dorther",
				"dorthin", "drauf", "drei", "dreißig", "drin", "dritte",
				"drunter", "drüber", "dunklen", "durchaus", "durfte",
				"durften", "dürfen", "dürfte", "eben", "ebenfalls", "ebenso",
				"ehe", "eher", "eigenen", "eigenes", "eigentlich", "einbaün",
				"einerseits", "einfach", "einführen", "einführte",
				"einführten", "eingesetzt", "einigermaßen", "eins",
				"einseitig", "einseitige", "einseitigen", "einseitiger",
				"einst", "einstmals", "einzig", "elf", "ende", "entsprechend",
				"entweder", "ergänze", "ergänzen", "ergänzte", "ergänzten",
				"erhalten", "erhielt", "erhielten", "erhält", "erneut", "erst",
				"erste", "ersten", "erster", "eröffne", "eröffnen", "eröffnet",
				"eröffnete", "eröffnetes", "etc", "etliche", "etwa", "fall",
				"falls", "fand", "ferner", "finden", "findest", "findet",
				"folgende", "folgenden", "folgender", "folgendes", "folglich",
				"for", "fordern", "fordert", "forderte", "forderten",
				"fortsetzen", "fortsetzt", "fortsetzte", "fortsetzten",
				"fragte", "frau", "frei", "freie", "freier", "freies", "fuer",
				"fünf", "gab", "ganzem", "gar", "gbr", "geb", "geben",
				"geblieben", "gebracht", "gedurft", "gegeben", "gehabt",
				"gehen", "geht", "gekommen", "gekonnt", "gemocht", "gemäss",
				"genommen", "genug", "gern", "gestern", "gestrige", "getan",
				"geteilt", "geteilte", "getragen", "gewissermaßen", "geworden",
				"ggf", "gib", "gibt", "gleich", "gleichwohl", "gleichzeitig",
				"gmbh", "gratulierte", "haette", "halb", "hallo", "hast",
				"hattest", "hattet", "heraus", "herein", "heute", "heutige",
				"hiermit", "hiesige", "hinein", "hinten", "hinterher", "hoch",
				"html", "http", "hundert", "hätt", "hätte", "hätten", "image",
				"immer", "indessen", "info", "infolge", "innen", "innerhalb",
				"insofern", "inzwischen", "irgend", "irgendeine", "irgendwas",
				"irgendwen", "irgendwer", "irgendwie", "irgendwo", "je", "jed",
				"jedenfalls", "jederlei", "jedoch", "jemand", "jenseits",
				"jährig", "jährige", "jährigen", "jähriges", "kam", "kannst",
				"kaum", "keinerlei", "koennen", "koennt", "koennte",
				"koennten", "komme", "kommen", "kommt", "konnten", "könn",
				"könnt", "könnten", "künftig", "lag", "lagen", "lassen",
				"laut", "lediglich", "leer", "legen", "legte", "legten",
				"leicht", "leider", "lesen", "letze", "letzten",
				"letztendlich", "letztens", "letztes", "letztlich", "lichten",
				"liegt", "liest", "links", "mag", "magst", "mal",
				"mancherorts", "manchmal", "med", "mehr", "mehrere", "meist",
				"meiste", "meisten", "meta", "mindestens", "mithin", "mochte",
				"morgen", "morgige", "muessen", "muesst", "musst", "mussten",
				"muß", "mußt", "möchte", "möchten", "möchtest", "mögen",
				"müssen", "müsste", "müssten", "müßte", "nachdem", "nacher",
				"nachhinein", "nahm", "natürlich", "neben", "nebenan",
				"nehmen", "nein", "neun", "nimm", "nimmt", "nirgendwo",
				"nutzen", "nutzt", "nutzung", "nächste", "nämlich",
				"nötigenfalls", "nützt", "oberhalb", "obgleich", "obschon",
				"obwohl", "oft", "online", "per", "pfui", "reagiere",
				"reagieren", "reagiert", "reagierte", "rechts", "regelmäßig",
				"rief", "rund", "sang", "sangen", "schließlich", "schon",
				"schreibe", "schreiben", "schreibens", "schreiber",
				"schwierig", "schätzen", "schätzt", "schätzte", "schätzten",
				"sechs", "sect", "sehrwohl", "sei", "seit", "seitdem", "seite",
				"seiten", "seither", "selber", "senke", "senken", "senkt",
				"senkte", "senkten", "setzen", "setzt", "setzte", "setzten",
				"sicherlich", "sieben", "siebte", "siehe", "sieht", "singen",
				"singt", "sobald", "sodaß", "soeben", "sofern", "sofort",
				"sog", "sogar", "solange", "solchen", "solch", "sollen",
				"sollst", "sollt", "sollten", "solltest", "somit", "sonstwo",
				"sooft", "soviel", "soweit", "sowie", "sowohl", "spielen",
				"später", "startet", "startete", "starteten", "statt",
				"stattdessen", "steht", "steige", "steigen", "steigt", "stets",
				"stieg", "stiegen", "such", "suchen", "sämtliche", "tages",
				"tat", "tatsächlich", "tatsächlichen", "tatsächlicher",
				"tatsächliches", "tausend", "teile", "teilen", "teilte",
				"teilten", "titel", "total", "trage", "tragen", "trotzdem",
				"trug", "trägt", "tun", "tust", "tut", "txt", "tät", "ueber",
				"umso", "unbedingt", "ungefähr", "unsem", "unser", "unsere",
				"unserem", "unseren", "unserer", "unseres", "unten",
				"unterbrach", "unterbrechen", "unterhalb", "unwichtig", "usw",
				"var", "vergangen", "vergangene", "vergangener", "vergangenes",
				"vermag", "vermutlich", "vermögen", "verrate", "verraten",
				"verriet", "verrieten", "version", "versorge", "versorgen",
				"versorgt", "versorgte", "versorgten", "versorgtes", "viele",
				"vielen", "vieler", "vieles", "vielleicht", "vielmals", "vier",
				"voran", "vorbei", "vorgestern", "vorher", "vorne", "völlig",
				"während", "wachen", "waere", "warum", "weder", "wegen",
				"weitere", "weiterem", "weiteren", "weiterer", "weiteres",
				"weiterhin", "weiß", "wem", "wen", "wenngleich", "wer",
				"werdet", "weshalb", "wessen", "wichtig", "wieso", "wieviel",
				"wiewohl", "willst", "wirklich", "wodurch", "wogegen", "woher",
				"wohin", "wohingegen", "wohl", "wohlweislich", "womit",
				"woraufhin", "woraus", "worin", "wurde", "wurden",
				"währenddessen", "wär", "wäre", "wären", "zahlreich", "zehn",
				"zeitweise", "ziehen", "zieht", "zog", "zogen", "zudem",
				"zuerst", "zufolge", "zugleich", "zuletzt", "zumal", "zurück",
				"zusammen", "zuviel", "zwanzig", "zwei", "zwölf", "ähnlich",
				"überall", "überallhin", "überdies", "übermorgen", "übrig",
				"übrigens" };

		public final String[] STOP_WORDS = new String[] { "ab", "aber", "Aber",
				"alle", "allein", "allem", "allen", "aller", "als", "Als",
				"also", "alt", "am", "Am", "an", "andere", "anderen",
				"arbeiten", "auch", "Auch", "auf", "Auf", "Aufgabe", "aus",
				"außer", "bald", "beginnen", "bei", "Bei", "beide", "beiden",
				"beim", "bekannt", "bekennen", "bereits", "berichten",
				"bestehen", "betonen", "betonte", "bin", "bis", "bißchen",
				"bisschen", "Bisschen", "bist", "bleiben", "bringen", "da",
				"dabei", "dadurch", "dafür", "dagegen", "dahinter", "damit",
				"danach", "daneben", "dann", "daran", "darauf", "daraus",
				"darin", "darüber", "darum", "darunter", "das", "Das", "daß",
				"dass", "Dass", "dasselbe", "davon", "davor", "dazu",
				"dazwischen", "dein", "deine", "deinem", "deinen", "deiner",
				"deines", "dem", "demselben", "den", "denen", "denn", "der",
				"Der", "deren", "derselben", "des", "desselben", "dessen",
				"deutlich", "dich", "die", "Die", "dies", "Dies", "diese",
				"Diese", "dieselbe", "dieselben", "diesem", "diesen", "dieser",
				"dieses", "dir", "doch", "Doch", "dort", "drei", "du", "durch",
				"dürfen", "ebenso", "eigen", "eigenen", "ein", "Ein", "eine",
				"Eine", "einem", "einen", "einer", "eines", "einig", "einige",
				"einigen", "einmal", "entlang", "entscheiden", "entsprechen",
				"EPD", "er", "Er", "erhalten", "erklären", "erklärte", "erst",
				"ersten", "es", "Es", "etwa", "etwas", "euch", "euer", "eure",
				"eurem", "euren", "eurer", "eures", "fest", "finden",
				"fordern", "fragen", "frei", "früh", "führen", "fünf", "für",
				"Für", "fürs", "ganz", "gar", "gebe", "geben", "gegen",
				"gegenüber", "gehen", "gehören", "geht", "gemeinsam", "genau",
				"gewesen", "gibt", "glauben", "gleich", "groß", "großen",
				"gründen", "gut", "habe", "haben", "handeln", "hat", "hatte",
				"hätte", "hatten", "hätten", "heilig", "heißt", "her",
				"herein", "herum", "heute", "hin", "hinter", "hintern", "hoch",
				"hören", "ich", "ihm", "ihn", "Ihnen", "ihnen", "ihr", "ihre",
				"Ihre", "ihrem", "Ihrem", "ihren", "Ihren", "Ihrer", "ihrer",
				"ihres", "Ihres", "im", "Im", "immer", "in", "In", "ins",
				"international", "ist", "ja", "je", "jedesmal", "jedoch",
				"jene", "jenem", "jenen", "jener", "jenes", "jetzt", "jung",
				"kann", "KAP", "kaum", "kein", "keine", "keinem", "keinen",
				"keiner", "keines", "kirchlich", "klein", "kommen", "könne",
				"können", "könnten", "kritisieren", "lang", "laß", "lass",
				"lassen", "leben", "letzen", "letzte", "letzten", "machen",
				"man", "mehr", "mein", "meine", "meinem", "meinen", "meiner",
				"meines", "meist", "mich", "mir", "mit", "Mit", "mitteilen",
				"möglich", "muß", "muss", "müsse", "müssen", "müßten",
				"müssten", "nach", "Nach", "nachdem", "nah", "nämlich",
				"national", "neben", "nehmen", "nein", "nennen", "neu", "neue",
				"neuen", "nicht", "nichts", "noch", "nun", "nur", "ob", "ober",
				"obgleich", "oder", "ohne", "paar", "Recht", "recht", "reich",
				"religiös", "rund", "sagte", "schaffen", "schon", "schreiben",
				"schwer", "sehen", "sehr", "sei", "seien", "sein", "seine",
				"seinem", "seinen", "seiner", "seines", "seit", "seitdem",
				"selbst", "Selbst", "setzen", "sich", "Sie", "sie", "sind",
				"so", "So", "sogar", "solch", "solche", "solchem", "solchen",
				"solcher", "solches", "soll", "sollen", "sollte", "sollten",
				"sondern", "sonst", "soviel", "soweit", "sowie", "spät",
				"sprechen", "stark", "stehen", "steht", "stellen", "teilen",
				"teilte", "über", "um", "und", "Und", "uns", "unser", "unsere",
				"unserem", "unseren", "unserer", "unseres", "unter",
				"vergangen", "vergangenen", "vergehen", "veröffentlichen",
				"viel", "viele", "vier", "voll", "vom", "von", "Von", "vor",
				"Vor", "vorsitzen", "währen", "während", "war", "wäre",
				"waren", "wären", "warum", "was", "wegen", "weil", "weit",
				"weiter", "welche", "welchem", "welchen", "welcher", "welches",
				"wem", "wen", "wenig", "wenige", "wenn", "Wenn", "wer",
				"werde", "werden", "weshalb", "wessen", "wichtig", "wie",
				"Wie", "wieder", "will", "wir", "Wir", "wird", "wo", "wollen",
				"womit", "worden", "wurde", "wurden", "würden", "zehn",
				"zeigen", "zentral", "zu", "Zu", "zum", "zur", "zwar", "zwei",
				"zweit", "zwischen", "zwischens" };

		private HashSet<String> stopWordSet = new HashSet<String>();

		public StopWordListGerman() {
			stopWordSet = new HashSet<String>(Arrays.asList(STOP_WORDS));
			stopWordSet.addAll(Arrays.asList(STOP_WORDS_SENTIMENT));
		}

		public boolean isStopword(String str) {
			return stopWordSet.contains(str.toLowerCase());
		}

	}

	/**
	 * Stopwords for the English language
	 * 
	 * @author Sebastian Land
	 */
	private class StopWordListEnglish implements StopWordList {

		private final String[] STOP_WORDS = new String[] { "a", "abaft",
				"aboard", "about", "above", "across", "afore", "aforesaid",
				"after", "again", "against", "agin", "ago", "aint", "albeit",
				"all", "almost", "alone", "along", "alongside", "already",
				"also", "although", "always", "am", "american", "amid",
				"amidst", "among", "amongst", "an", "and", "anent", "another",
				"any", "anybody", "anyone", "anything", "are", "aren",
				"around", "as", "aslant", "astride", "at", "athwart", "away",
				"back", "bar", "barring", "be", "because", "been", "before",
				"behind", "being", "below", "beneath", "beside", "besides",
				"best", "better", "between", "betwixt", "beyond", "both",
				"but", "by", "can", "cannot", "certain", "circa", "close",
				"concerning", "considering", "cos", "could", "couldn",
				"couldst", "dare", "dared", "daren", "dares", "daring",
				"despite", "did", "didn", "different", "directly", "do",
				"does", "doesn", "doing", "done", "don", "dost", "doth",
				"down", "during", "durst", "each", "early", "either", "em",
				"english", "enough", "ere", "even", "ever", "every",
				"everybody", "everyone", "everything", "except", "excepting",
				"failing", "far", "few", "first", "five", "following", "for",
				"four", "from", "gonna", "gotta", "had", "hadn", "hard", "has",
				"hasn", "hast", "hath", "have", "haven", "having", "he", "her",
				"here", "hers", "herself", "high", "him", "himself", "his",
				"home", "how", "howbeit", "however", "id", "if", "ill",
				"immediately", "important", "in", "inside", "instantly",
				"into", "is", "isn", "it", "its", "itself", "ve", "just",
				"large", "last", "later", "least", "left", "less", "lest",
				"let", "like", "likewise", "little", "living", "long", "many",
				"may", "mayn", "me", "mid", "midst", "might", "mightn", "mine",
				"minus", "more", "most", "much", "must", "mustn", "my",
				"myself", "near", "neath", "need", "needed", "needing",
				"needn", "needs", "neither", "never", "nevertheless", "new",
				"next", "nigh", "nigher", "nighest", "nisi", "no", "one",
				"nobody", "none", "nor", "not", "nothing", "notwithstanding",
				"now", "er", "of", "off", "often", "on", "once", "oneself",
				"only", "onto", "open", "or", "other", "otherwise", "ought",
				"oughtn", "our", "ours", "ourselves", "out", "outside", "over",
				"own", "past", "pending", "per", "perhaps", "plus", "possible",
				"present", "probably", "provided", "providing", "public",
				"qua", "quite", "rather", "re", "real", "really", "respecting",
				"right", "round", "same", "sans", "save", "saving", "second",
				"several", "shall", "shalt", "shan", "she", "shed", "shell",
				"short", "should", "shouldn", "since", "six", "small", "so",
				"some", "somebody", "someone", "something", "sometimes",
				"soon", "special", "still", "such", "summat", "supposing",
				"sure", "than", "that", "the", "thee", "their", "theirs",
				"them", "themselves", "then", "there", "these", "they",
				"thine", "this", "tho", "those", "thou", "though", "three",
				"thro", "through", "throughout", "thru", "thyself", "till",
				"to", "today", "together", "too", "touching", "toward",
				"towards", "true", "twas", "tween", "twere", "twill", "twixt",
				"two", "twould", "under", "underneath", "unless", "unlike",
				"until", "unto", "up", "upon", "us", "used", "usually",
				"versus", "very", "via", "vice", "vis-a-vis", "wanna",
				"wanting", "was", "wasn", "way", "we", "well", "were", "weren",
				"wert", "what", "whatever", "when", "whencesoever", "whenever",
				"whereas", "where", "whether", "which", "whichever",
				"whichsoever", "while", "whilst", "who", "whoever", "whole",
				"whom", "whore", "whose", "whoso", "whosoever", "will", "with",
				"within", "without", "wont", "would", "wouldn", "wouldst",
				"ye", "yet", "you", "your", "yours", "yourself", "yourselves" };

		public final HashSet<String> STOPWORD_SET = new HashSet<String>(
				Arrays.asList(STOP_WORDS));

		public boolean isStopword(String str) {
			return STOPWORD_SET.contains(str.toLowerCase());
		}

	}

	/**
	 * A standard stopword operator for German texts, which removes every token
	 * equal to a stopword.
	 * 
	 * @author Sebastian Land
	 */
	private List<Token> doWork(List<Token> tokens, StopWordList stopwords) {

		List<Token> newSequence = new LinkedList<Token>();

		for (Token token : tokens) {
			if (!stopwords.isStopword(token.getWord()))
				newSequence.add(token);
		}

		return newSequence;
	}
}
