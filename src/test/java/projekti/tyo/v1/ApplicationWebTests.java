package projekti.tyo.v1;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationWebTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void homePageIsPublic() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Helpotus Arkeen")));
    }

    @Test
    void dashboardRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/dashboard"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedUserCanOpenDashboard() throws Exception {
        mockMvc.perform(get("/dashboard").with(user("mira").roles("USER")))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Hallintapaneeli")));
    }

    @Test
    void publicRecipeApiReturnsJson() throws Exception {
        mockMvc.perform(get("/api/recipes"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("application/json"));
    }

    @Test
    void userCanRegister() throws Exception {
        mockMvc.perform(post("/register")
                .param("fullName", "Testi Käyttäjä")
                .param("username", "testaaja123")
                .param("email", "testaaja@example.com")
                .param("password", "salasana123")
                .param("confirmPassword", "salasana123"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"))
            .andExpect(flash().attributeExists("successMessage"));
    }
}
