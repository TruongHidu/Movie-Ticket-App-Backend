package com.example.movie_ticket_.controller.admin;

import com.example.movie_ticket_.entity.PaymentMethod;
import com.example.movie_ticket_.service.CloudinaryService;
import com.example.movie_ticket_.service.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Controller
@RequestMapping("/admin/paymentMethod")
@RequiredArgsConstructor
public class PaymentMethodAdminController {

    private final PaymentMethodService paymentMethodService;
    private final CloudinaryService cloudinaryService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("methods", paymentMethodService.getAll());
        return "paymentMethod/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("method", new PaymentMethod());
        return "paymentMethod/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute PaymentMethod method,
                      @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                Map uploadResult = cloudinaryService.upload(imageFile);
                String imageUrl = (String) uploadResult.get("secure_url");
                method.setImageUrl(imageUrl);
            }
            paymentMethodService.add(method);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin/paymentMethod";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        PaymentMethod method = paymentMethodService.getById(id);
        if (method == null) {
            return "redirect:/admin/paymentMethod";
        }
        model.addAttribute("method", method);
        return "paymentMethod/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
                       @RequestParam("nameMethod") String nameMethod,
                       @RequestParam("status") int status,
                       @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            PaymentMethod method = paymentMethodService.getById(id);
            if (method != null) {
                method.setNameMethod(nameMethod);
                method.setStatus(status);

                if (imageFile != null && !imageFile.isEmpty()) {
                    Map uploadResult = cloudinaryService.upload(imageFile);
                    String imageUrl = (String) uploadResult.get("secure_url");
                    method.setImageUrl(imageUrl);
                }

                paymentMethodService.save(method);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin/paymentMethod";
    }

    @PostMapping("/status/{id}")
    public String changeStatus(@PathVariable Long id) {
        PaymentMethod method = paymentMethodService.getById(id);
        if (method != null) {
            int newStatus = method.getStatus() == 1 ? 0 : 1;
            paymentMethodService.changeStatus(id, newStatus);
        }
        return "redirect:/admin/paymentMethod";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        paymentMethodService.delete(id);
        return "redirect:/admin/paymentMethod";
    }
}
