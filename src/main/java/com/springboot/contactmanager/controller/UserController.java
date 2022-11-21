package com.springboot.contactmanager.controller;

import com.springboot.contactmanager.entities.Contact;
import com.springboot.contactmanager.entities.User;
import com.springboot.contactmanager.helper.Message;
import com.springboot.contactmanager.repository.ContactRepository;
import com.springboot.contactmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

@Configuration
@RequestMapping("/user")
public class UserController {
    private final UserRepository userRepo;
    private final ContactRepository contactRepo;

    @Autowired
    public UserController(UserRepository userRepo, ContactRepository contactRepo) {
        this.userRepo = userRepo;
        this.contactRepo = contactRepo;
    }

    /*
        *this method is for getting the data from the user, in any route that it had fired.
        * note: it was used in profile handler!
    */
    @ModelAttribute
    public void getUserData(Model model, Principal principal) {
        User user = userRepo.findByEmail(principal.getName());
        model.addAttribute("user", user);
    }

    /*home page*/
    @RequestMapping("/index")
    private String dashboard(Model model) {
        model.addAttribute("title", "Dashboard");
        return "normal/user_dashboard";
    }

    /*Add contact 1*/
    @GetMapping("/add-contact")
    public String addContact(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "normal/add-contact";
    }

    /*Add contact 2*/
    @PostMapping("/process-contact")
    public String doAddContact(@Valid @ModelAttribute Contact contact, BindingResult bindingResult,
                               HttpSession session,
                               Model model,
                               @RequestParam("profileImage") MultipartFile file,
                               Principal principal) {
        System.out.println("\n---------------------------\n" + contact + "\n---------------------------\n");

        try {
            if (bindingResult.hasErrors()) { //field validation
                model.addAttribute("contact", contact);
                throw new Exception("Uh oh! something went wrong");
            }

            //get the user that is logged in
            String principalName = principal.getName();
            User user = userRepo.findByEmail(principalName);


            /* uploading image */
            if (file.isEmpty()) {
                System.out.println("image empty");
                contact.setImage("contact.png");
            } else {
                //upload the file(image) to the folder, and save it in contact db
                contact.setImage(file.getOriginalFilename());
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }


            /** mapping user and contact 👇 **/
            //set the very same user in the contact, so it'll have the same parent and id
            contact.setUser(user);
            //add the contact that is coming into that very same user
            user.getContacts().add(contact);

            /** mapping user and contact 👆 */


            //save the contact in that very same user in the database
            userRepo.save(user);
            session.setAttribute("message", new Message("Successfully added ;)", "alert-success"));
            model.addAttribute("contact", new Contact());
            return "normal/add-contact";

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("error?: " + e.getMessage());
            session.setAttribute("message", new Message("Uh oh! ", "alert-danger"));
            model.addAttribute("contact", contact);
            return "normal/add-contact";
        }
    }

    /*show all contacts*/
    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") int page, Model model, Principal principal) {
        model.addAttribute("title", "Show all contacts");
        //get the user
        String name = principal.getName();
        User user = userRepo.findByEmail(name);

        Pageable pageable = PageRequest.of(page, 3);

        Page<Contact> contacts = contactRepo.getContactByUserId(user.getId(), pageable);
        //send the very same user id to the model
        model.addAttribute("contacts", contacts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contacts.getTotalPages());
        return "normal/show-contacts";
    }

    /*show single contact*/
    @GetMapping("/{contactId}/contacts")
    public String showSingleContact(@PathVariable("contactId") Integer contactId, Model model, Principal principal) {

        Contact contact = contactRepo.findById(contactId).get();

        String name = principal.getName();
        User loggedInUser = userRepo.findByEmail(name);

        /*for securing the contacts, so that only the contacts for the logged-in user will be shown.*/
        /*instead of this method, we can declare the method as POST, and it'll get automatically secured*/
        if (loggedInUser.getId() == contact.getUser().getId()) {
            model.addAttribute("contact", contact);
            model.addAttribute("title", contact.getName());
        }
        return "normal/show-single-contact";
    }

    /*delete a contact*/
    @GetMapping("/{contactId}/delete")
    public String deleteContact(@PathVariable("contactId") Integer contactId, Model model, Principal principal, HttpSession session) {
        User loggedInUser = userRepo.findByEmail(principal.getName());
        Contact contact = contactRepo.findById(contactId).get();

        /*for securing the contacts, so that only the contacts for the logged-in user will be shown.*/
        if (loggedInUser.getId() == contact.getUser().getId()) {
            /** contact will not get deleted bcuz in User class we associated the contact with "CascadeType.ALL" i.e. we made all the cascade true.
              So we'll unlink the contact with the user and then delete it. */
            contact.setUser(null);
            /** now it'll delete (not actual deletion only getting unlinked of user) **/
            contactRepo.delete(contact);

//            User user = userRepo.findByEmail(principal.getName());
//            user.getContacts().remove(contact);
            session.setAttribute("message", new Message("Deleted successfully", " alert-success"));
        }
        return "redirect:/user/show-contacts/0";
    }

    /*update a contact form handler - 1*/
    /*using this method, there will be no need to secure with all the if's in like in delete handler.*/
    @PostMapping("/update-contact/{contactId}")
    public String updateContact(@PathVariable("contactId") Integer contactId, Model model) {
        model.addAttribute("title", "Update Contact");

        Contact contact = contactRepo.findById(contactId).get();
        model.addAttribute("contact", contact);
        return "normal/update-contact";
    }

    /*update a contact - 1.2*/
    @PostMapping("/process-update")
    public String doUpdateContact(@ModelAttribute Contact contact,
                                  @RequestParam("profileImage") MultipartFile file,
                                  Principal principal,
                                  Model model,
                                  HttpSession session) {

        try {
            Contact oldContact = contactRepo.findById(contact.getContactId()).get();
            if (!file.isEmpty()) {
                /*if image was there, work on image*/
                //delete old photo
                File deleteFilePath = new ClassPathResource("static/img").getFile();
                File fileAppend = new File(deleteFilePath, oldContact.getImage());
                boolean delete = fileAppend.delete();
                if (delete) System.out.println("old image deleted");

                //update new photo
                contact.setImage(file.getOriginalFilename());
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            } else {
                contact.setImage(oldContact.getImage());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        //set the contact to the same user since it unlinked it
        User user = userRepo.findByEmail(principal.getName());
        contact.setUser(user);
        contactRepo.save(contact);
        session.setAttribute("message", new Message("Updated Successfully", "alert-success"));

        System.out.println("\nupdate contact: " + contact.getName());
        System.out.println("update contact: " + contact.getContactId());
        return "redirect:/user/" + contact.getContactId() + "/contacts";
    }

    //profile
    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("title", "User profile");
        return "normal/profile";
    }
}
