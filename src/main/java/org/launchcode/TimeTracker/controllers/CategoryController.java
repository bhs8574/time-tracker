package org.launchcode.TimeTracker.controllers;

import org.launchcode.TimeTracker.data.CategoryRepository;
import org.launchcode.TimeTracker.models.Activity;
import org.launchcode.TimeTracker.models.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("create")
    public String displayCreateActivityForm(Model model) {
        model.addAttribute("title", "Create Category");
        model.addAttribute(new Category());
        return "categories/create";
    }

    @PostMapping("create")
    public String processCreateActivityForm(@ModelAttribute @Valid Category newCategory,
                                            Errors errors, Model model) {
        if(errors.hasErrors()) {
            model.addAttribute("title", "Create Category");
            return "categories/create";
        }

        categoryRepository.save(newCategory);
        return "redirect:../activities/create";
    }

}
