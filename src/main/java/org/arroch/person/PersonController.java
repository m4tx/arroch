package org.arroch.person;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Random;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/person/{personId}")
public class PersonController {
    @GetMapping
    public String person(@PathVariable int personId, Model model) {
        model.addAttribute("person", getRandomPerson(personId));
        return "person";
    }

    private static Person getRandomPerson(long seed) {
        Random random = new Random(seed);
        String firstName = randomString(random, 10);
        String lastName = randomString(random, 10);
        return new Person(firstName, "", lastName, firstName + " " + lastName);
    }

    private static String randomString(Random random, final int maxLength) {
        final int length = 1 + random.nextInt(maxLength);
        return random.ints(length, 'a', 'z' + 1)
                .mapToObj(i -> Character.toString((char) i))
                .collect(Collectors.joining());
    }
}
