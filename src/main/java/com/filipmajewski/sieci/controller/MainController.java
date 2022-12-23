package com.filipmajewski.sieci.controller;

import com.filipmajewski.sieci.container.OfferContainer;
import com.filipmajewski.sieci.entity.Device;
import com.filipmajewski.sieci.entity.User;
import com.filipmajewski.sieci.repository.DeviceRepository;
import com.filipmajewski.sieci.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    private UserRepository userRepository;

    private DeviceRepository deviceRepository;

    @Autowired
    public MainController(UserRepository userRepository, DeviceRepository deviceRepository) {
        this.userRepository = userRepository;
        this.deviceRepository = deviceRepository;
    }

    @RequestMapping({"/", "/home"})
    public String homePage() {
        return "index.html";
    }

    @RequestMapping("/oferty")
    public ModelAndView offerPage() {
        ModelAndView mv = new ModelAndView("offers.html");

        List<Device> offerList = deviceRepository.findAll();
        List<OfferContainer> offers = new ArrayList<>();

        for(Device d : offerList) {
            User user = userRepository.findById(d.getUserID()).orElse(null);

            if(user != null) {
                offers.add(new OfferContainer(user, d));
            }

        }

        mv.addObject("offers", offers);

        return mv;
    }

    @RequestMapping("/profil")
    public ModelAndView profilePage() {
        ModelAndView mv = new ModelAndView("profil.html");

        User user = getAuthenticatedUser();

        List<Device> offerList = deviceRepository.findAllByUserID(user.getId());

        mv.addObject("username", user.getUsername());
        mv.addObject("phone", user.getPhone());
        mv.addObject("offerNumber", offerList.size());

        return mv;
    }

    @RequestMapping(value = "/nowa-oferta")
    public String addNewOffer() {
        return "addOffer.html";
    }

    @RequestMapping(value = "/addNewOffer", method = RequestMethod.POST)
    public ModelAndView addOffer(@RequestParam("name") String name,
                                 @RequestParam("description") String description,
                                 @RequestParam("price") int price) {

        ModelAndView mv = new ModelAndView("redirect:/oferty");

        User user = getAuthenticatedUser();

        Device newDevice = new Device(name, user.getId(),description, price);

        deviceRepository.save(newDevice);

        return mv;
    }

    @RequestMapping("/oferta")
    public ModelAndView offerDetails(@RequestParam int id) {
        ModelAndView mv = new ModelAndView("info.html");

        Device device = deviceRepository.findById(id).orElse(null);

        User user = userRepository.findById(device.getUserID()).orElse(null);

        mv.addObject("offer", new OfferContainer(user, device));

        return mv;
    }

    @RequestMapping(value = "/removeOffer")
    public String removeOffer(@RequestParam int id) {

        if(getAuthenticatedUser().getRole().equalsIgnoreCase("admin")) {
            deviceRepository.deleteById(id);
        }

        return "redirect:/oferty";
    }

    private User getAuthenticatedUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {

            String currentUserName = authentication.getName();

            return userRepository.findByUsername(currentUserName);
        }

        return null;
    }

}
