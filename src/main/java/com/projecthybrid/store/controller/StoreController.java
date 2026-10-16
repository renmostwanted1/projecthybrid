package com.projecthybrid.store.controller;

import com.projecthybrid.store.model.Product;
import com.projecthybrid.store.model.StoreOrder;
import com.projecthybrid.store.repository.ProductRepository;
import com.projecthybrid.store.repository.OrderRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.*;

@Controller
public class StoreController {
    private final ProductRepository products;
    private final OrderRepository orders;
    public StoreController(ProductRepository products, OrderRepository orders){this.products=products;this.orders=orders;}

    @ModelAttribute("cartCount")
    public int cartCount(HttpSession session) {
        Map<Long,Integer> cart = cart(session);
        return cart.values().stream().mapToInt(Integer::intValue).sum();
    }
    @SuppressWarnings("unchecked")
    private Map<Long,Integer> cart(HttpSession s) {
        Map<Long,Integer> c=(Map<Long,Integer>)s.getAttribute("cart");
        if(c==null){c=new LinkedHashMap<>();s.setAttribute("cart",c);} return c;
    }

    @GetMapping("/") public String home(@RequestParam(required=false) String q, Model model){
        List<Product> list=products.findByActiveTrueOrderByIdDesc();
        if(q!=null&&!q.isBlank()) list=list.stream().filter(p->(p.getName()!=null&&p.getName().toLowerCase().contains(q.toLowerCase())) ||
          (p.getCategory()!=null&&p.getCategory().toLowerCase().contains(q.toLowerCase()))).toList();
        model.addAttribute("products",list);model.addAttribute("q",q==null?"":q);return "index";
    }

    @GetMapping("/login") public String login(){ return "login"; }

    @GetMapping("/admin/products") public String admin(Model model){
        model.addAttribute("products",products.findAll());model.addAttribute("product",new Product());return "admin";
    }
    @PostMapping("/admin/products") public String addProduct(@ModelAttribute Product product, RedirectAttributes ra){
        if(product.getName()==null||product.getName().isBlank()||product.getPrice()==null||product.getPrice().signum()<0){
            ra.addFlashAttribute("error","Enter a product name and a valid price.");return "redirect:/admin/products";
        }
        if(product.getCategory()==null||product.getCategory().isBlank()) product.setCategory("Apparel");
        if(product.getImageUrl()==null||product.getImageUrl().isBlank()) product.setImageUrl("https://images.unsplash.com/photo-1523398002811-999ca8dec234?auto=format&fit=crop&w=900&q=80");
        product.setActive(true);products.save(product);ra.addFlashAttribute("message","Product added successfully.");return "redirect:/admin/products";
    }
    @PostMapping("/admin/products/{id}/delete") public String deleteProduct(@PathVariable Long id, RedirectAttributes ra){
        products.findById(id).ifPresent(p->{p.setActive(false);products.save(p);});
        ra.addFlashAttribute("message","Product hidden from the shop.");return "redirect:/admin/products";
    }

    @PostMapping("/cart/add/{id}") public String addToCart(@PathVariable Long id, HttpSession session, RedirectAttributes ra){
        Product p=products.findById(id).orElse(null);
        if(p==null||!p.isActive()){ra.addFlashAttribute("error","Product is unavailable.");return "redirect:/";}
        Map<Long,Integer> c=cart(session);int current=c.getOrDefault(id,0);
        if(current>=p.getStock()){ra.addFlashAttribute("error","There is not enough stock available.");return "redirect:/";}
        c.put(id,current+1);ra.addFlashAttribute("message","Added to your cart.");return "redirect:/";
    }
    @GetMapping("/cart") public String cartPage(HttpSession session, Model model){
        Map<Long,Integer> c=cart(session);List<Map<String,Object>> lines=new ArrayList<>();BigDecimal total=BigDecimal.ZERO;
        for(var entry:c.entrySet()){
            Product p=products.findById(entry.getKey()).orElse(null);
            if(p!=null&&p.isActive()){BigDecimal sub=p.getPrice().multiply(BigDecimal.valueOf(entry.getValue()));total=total.add(sub);
                Map<String,Object> line=new HashMap<>();line.put("product",p);line.put("quantity",entry.getValue());line.put("subtotal",sub);lines.add(line);}
        }
        model.addAttribute("lines",lines);model.addAttribute("total",total);return "cart";
    }
    @PostMapping("/cart/remove/{id}") public String remove(@PathVariable Long id,HttpSession session){cart(session).remove(id);return "redirect:/cart";}
    @GetMapping("/checkout") public String checkout(HttpSession session,Model model){
        if(cart(session).isEmpty()) return "redirect:/cart"; model.addAttribute("total",cartTotal(session));return "checkout";
    }
    private BigDecimal cartTotal(HttpSession s){BigDecimal t=BigDecimal.ZERO;for(var e:cart(s).entrySet()){
        Product p=products.findById(e.getKey()).orElse(null);if(p!=null)t=t.add(p.getPrice().multiply(BigDecimal.valueOf(e.getValue())));}return t;}
    @PostMapping("/checkout") public String placeOrder(@RequestParam String customerName,@RequestParam String email,
        @RequestParam String phone,@RequestParam String shippingAddress,HttpSession session,RedirectAttributes ra){
        Map<Long,Integer> c=cart(session);
        if(c.isEmpty())return "redirect:/cart";
        if(customerName.isBlank()||email.isBlank()||phone.isBlank()||shippingAddress.isBlank()){
            ra.addFlashAttribute("error","Please complete all checkout fields.");return "redirect:/checkout";}
        StringBuilder summary=new StringBuilder();BigDecimal total=BigDecimal.ZERO;
        for(var e:c.entrySet()){Product p=products.findById(e.getKey()).orElse(null);if(p==null||!p.isActive())continue;
            if(e.getValue()>p.getStock()){ra.addFlashAttribute("error","Stock changed for "+p.getName()+". Please review your cart.");return "redirect:/cart";}
            summary.append(p.getName()).append(" x ").append(e.getValue()).append("; ");
            total=total.add(p.getPrice().multiply(BigDecimal.valueOf(e.getValue())));
            p.setStock(p.getStock()-e.getValue());products.save(p);
        }
        StoreOrder order=orders.save(new StoreOrder(customerName,email,phone,shippingAddress,summary.toString(),total));
        c.clear();return "success";
    }
}