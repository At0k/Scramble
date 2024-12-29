package asia.fourtitude.interviewq.jumble.controller;

import java.time.ZonedDateTime;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import asia.fourtitude.interviewq.jumble.core.JumbleEngine;
import asia.fourtitude.interviewq.jumble.model.ExistsForm;
import asia.fourtitude.interviewq.jumble.model.PrefixForm;
import asia.fourtitude.interviewq.jumble.model.ScrambleForm;
import asia.fourtitude.interviewq.jumble.model.SearchForm;
import asia.fourtitude.interviewq.jumble.model.SubWordsForm;

@Controller
@RequestMapping(path = "/")
public class RootController {

    private static final Logger LOG = LoggerFactory.getLogger(RootController.class);

    private final JumbleEngine jumbleEngine;

    @Autowired(required = true)
    public RootController(JumbleEngine jumbleEngine) {
        this.jumbleEngine = jumbleEngine;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("timeNow", ZonedDateTime.now());
        return "index";
    }

    @GetMapping("scramble")
    public String doGetScramble(Model model) {
        model.addAttribute("form", new ScrambleForm());
        return "scramble";
    }

    @PostMapping("scramble")
public String doPostScramble(
        @ModelAttribute(name = "form") ScrambleForm form,
        BindingResult bindingResult, Model model) {
    if (form.getWord() == null || form.getWord().trim().isEmpty()) {
        bindingResult.rejectValue("word", "error.form", "Please enter a word.");
        return "scramble";
    }
    
    String scrambledWord = jumbleEngine.scramble(form.getWord());
    model.addAttribute("scrambledWord", scrambledWord);
    
    return "scramble";
    }

    @GetMapping("palindrome")
    public String doGetPalindrome(Model model) {
        model.addAttribute("words", this.jumbleEngine.retrievePalindromeWords());
        return "palindrome";
    }

    @GetMapping("exists")
    public String doGetExists(Model model) {
        model.addAttribute("form", new ExistsForm());
        return "exists";
    }

    @PostMapping("exists")
    public String doPostExists(
        @ModelAttribute(name = "form") ExistsForm form,
        BindingResult bindingResult, Model model) {
    if (form.getWord() == null || form.getWord().trim().isEmpty()) {
        bindingResult.rejectValue("word", "error.form", "Please enter a word.");
        return "exists";
    }
    
    boolean exists = jumbleEngine.exists(form.getWord());
    model.addAttribute("exists", exists);
    
    return "exists";
    }

    @GetMapping("prefix")
    public String doGetPrefix(Model model) {
        model.addAttribute("form", new PrefixForm());
        return "prefix";
    }

    @PostMapping("prefix")
    public String doPostPrefix(
        @ModelAttribute(name = "form") PrefixForm form,
        BindingResult bindingResult, Model model) {
    if (form.getPrefix() == null || form.getPrefix().trim().isEmpty()) {
        bindingResult.rejectValue("prefix", "error.form", "Please enter a prefix.");
        return "prefix";
    }

    Collection<String> words = jumbleEngine.wordsMatchingPrefix(form.getPrefix());
    model.addAttribute("words", words);

    return "prefix";
    }

    @GetMapping("search")
    public String doGetSearch(Model model) {
        model.addAttribute("form", new SearchForm());
        return "search";
    }

    @PostMapping("search")
    public String doPostSearch(
            @ModelAttribute(name = "form") SearchForm form,
            BindingResult bindingResult, Model model) {
        boolean hasErrors = false;
    
        if (form.getStartChar() != null && !Character.isLetter(form.getStartChar())) {
            bindingResult.rejectValue("startChar", "error.form", "Invalid startChar");
            hasErrors = true;
        }
    
        if (form.getEndChar() != null && !Character.isLetter(form.getEndChar())) {
            bindingResult.rejectValue("endChar", "error.form", "Invalid endChar");
            hasErrors = true;
        }
    
        if (form.getLength() != null && form.getLength() < 1) {
            bindingResult.rejectValue("length", "error.form", "Invalid length");
            hasErrors = true;
        }
    
        if (hasErrors) {
            return "search";
        }
    
        Collection<String> words = jumbleEngine.searchWords(form.getStartChar(), form.getEndChar(), form.getLength());
        model.addAttribute("words", words);
    
        return "search";
    }
    
    @GetMapping("subWords")
    public String goGetSubWords(Model model) {
        model.addAttribute("form", new SubWordsForm());
        return "subWords";
    }

    @PostMapping("subWords")
    public String doPostSubWords(
            @ModelAttribute(name = "form") SubWordsForm form,
            BindingResult bindingResult, Model model) {
        if (form.getWord() == null || form.getWord().trim().isEmpty()) {
            bindingResult.rejectValue("word", "error.form", "Please enter a word.");
            return "subWords";
        }
    
        if (form.getMinLength() == null || form.getMinLength() < 1) {
            bindingResult.rejectValue("minLength", "error.form", "Invalid minLength");
            return "subWords";
        }
    
        Collection<String> subWords = jumbleEngine.generateSubWords(form.getWord(), form.getMinLength());
        model.addAttribute("subWords", subWords);
    
        return "subWords";
    }
    


}
