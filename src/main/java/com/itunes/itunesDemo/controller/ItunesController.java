package com.itunes.itunesDemo.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itunes.itunesDemo.Artist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

import java.util.List;

@RestController
@RequestMapping("/itunesDemo")
public class ItunesController {

    @Value("${itunes.endpoint}")
    String itunesUrl;

    Logger logger = LoggerFactory.getLogger(this.getClass());



    @GetMapping
    public Object getArtistData(@RequestParam(value="firstName") String firstName,
                                @RequestParam(value="lastName") String lastName){

        List<Artist> artistList = new ArrayList<>();

      try{
          if (checkParameters(firstName, lastName)){
              RestTemplate restTemplate = new RestTemplate();

               itunesUrl= itunesUrl+"term="+firstName +"+"+lastName;

              ResponseEntity<String> response = restTemplate.getForEntity(itunesUrl, String.class);
              ObjectMapper mapper = new ObjectMapper();
              JsonNode root = mapper.readTree(response.getBody());
              JsonNode results = root.get("results");

              if (results.isArray()){
                  results.iterator().forEachRemaining(node -> {
                      boolean sameArtist=true;
                      if(!artistList.isEmpty()){
                          sameArtist = artistList.get(0).getArtistName().equalsIgnoreCase(node.get("artistName").asText());
                      }
                      if(sameArtist) {
                          Artist artist = new Artist();
                          artist.setArtistName(node.get("artistName").asText());
                          artist.setCollectionName(node.get("collectionName").asText());
                          artist.setPrimaryGenreName(node.get("primaryGenreName").asText());
                          artist.setTrackName(node.get("trackName").asText());
                          artistList.add(artist);
                      }
                  });


              }

          }

      } catch (Exception e){
        logger.error("Exception getting itunes info: " + e);
      }


      return artistList;
    }

    private boolean checkParameters(String firstName, String lastName){
        if(!StringUtils.isEmpty(firstName) &&
        !StringUtils.isEmpty(lastName) && !StringUtils.isEmpty(itunesUrl)){
            return true;
        }
        logger.error("One of the parameters or the itunes address is empty");
        return false;
    }



}
