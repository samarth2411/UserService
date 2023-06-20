package com.samarth.user.service.services.Impl;

import com.samarth.user.service.Exceptions.ResourceNotFoundException;
import com.samarth.user.service.entitites.Hotel;
import com.samarth.user.service.entitites.Rating;
import com.samarth.user.service.entitites.User;
import com.samarth.user.service.externalFeignServices.services.HotelService;
import com.samarth.user.service.repositories.UserRepository;
import com.samarth.user.service.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.module.ResolutionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HotelService hotelService;

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public User saveUser(User user) {
        String randomUserId = UUID.randomUUID().toString();
        user.setUserId(randomUserId);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    // this will fetch single user but also along with the user we have to fetch the ratings given by that user
    @Override
    public User getUser(String userId) {
        User user =  userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with given id not found !!" +userId));

        // now we will fetch the ratings given by that user from RATING SERVICE using the below url
        // http://localhost:8073/ratings/users/c0d077c5-4231-4274-b91d-4a3b71aecc1c --> at the last we have passed the userId

        Rating[] ratingOfUser = restTemplate.getForObject("http://RATING-SERVICE/ratings/users/"+user.getUserId() , Rating[].class);
        logger.info("{}",ratingOfUser);

        List<Rating> ratings = Arrays.stream(ratingOfUser).toList();

        List<Rating> ratingListWithHotels = ratings.stream().map(rating -> {
            // api call to hotel service to get the hotel of that particular rating

            // http://localhost:8072/hotels/8cc95514-36ba-44fb-8dad-a73a22f682b5


            // using rest template
//            ResponseEntity<Hotel> entity =  restTemplate.getForEntity("http://HOTEL-SERVICE/hotels/"+rating.getHotelId(), Hotel.class);
//            Hotel hotel = entity.getBody();

            // using feign client
            Hotel hotel = hotelService.getHotel(rating.getHotelId());

            // then set the hotel to the rating
            rating.setHotel(hotel);

            return rating;
        }).collect(Collectors.toList());

        user.setRatings(ratingListWithHotels);
        return user;
    }

    @Override
    public boolean deleteUser(String userId) {
        return false;
    }

    @Override
    public User updateUser(String userId) {
        return null;
    }
}
