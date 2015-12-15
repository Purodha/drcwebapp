package de.uni_koeln.spinfo.drc.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import de.uni_koeln.spinfo.drc.mongodb.DataBase;


@Controller()
@RequestMapping(value = "/drc")
public class SpellCheckController {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private DataBase db;


	@RequestMapping(value = "/pages")
	public ModelAndView getPages(@RequestParam("chapterId") String chapterId,
			@RequestParam("chapterTitle") String chapterTitle,
			@RequestParam("volumeId") String volumeId,
			@RequestParam("volumeTitle") String volumeTitle) {
		
		List<Page> pages = db.getPageRepository().findByChapterIds(Arrays.asList(new String[] { chapterId }));
		
		ModelAndView mv = new ModelAndView(Constants.PAGES);
		mv.addObject("pages", pages);
		mv.addObject("chapterTitle", chapterTitle);
		mv.addObject("chapterId", chapterId);
		mv.addObject("volumeId", volumeId);
		mv.addObject("volumeTitle", volumeTitle);	
		
		return mv;
	}

	@RequestMapping(value = "/page")
	public ModelAndView pageDetails(@RequestParam("pageId") String pageId, 
			@RequestParam("chapterId") String chapterId,
			@RequestParam("chapterTitle") String chapterTitle,
			@RequestParam("volumeId") String volumeId,
			@RequestParam("volumeTitle") String volumeTitle) {
		
		Page page = db.getPageRepository().findByPageId(pageId);
		List<Word> words = db.getWordRepository().findByRange(page.getStart(), page.getEnd());
		
		Collections.sort(words);

		ModelAndView mv = new ModelAndView(Constants.PAGE);
		mv.addObject("words", words);
		mv.addObject("page", page);
		mv.addObject("chapterTitle", chapterTitle);
		mv.addObject("chapterId", chapterId);
		mv.addObject("chapters", getChapters(page));
		mv.addObject("volumeId", volumeId);
		mv.addObject("volumeTitle", volumeTitle);
		
		return mv;
	}
	
	public List<Chapter> getChapters(Page page) {
		
		List<String> chapterIds = page.getChapterIds();
		List<Chapter> chapters = new ArrayList<>();
		
		for (String chapterId : chapterIds) {
			chapters.add(db.getChapterRepository().findOne(chapterId));
		}
		
		return chapters;
	}
	
	@RequestMapping(value = "/language", method = RequestMethod.GET)
	@ResponseBody
	public String getLanguage(@RequestParam("languageId") String languageId) {
		return db.getLanguageRepository().findOne(languageId).getTitle();
	}
	
}
