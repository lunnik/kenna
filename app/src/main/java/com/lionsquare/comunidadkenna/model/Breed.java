package com.lionsquare.comunidadkenna.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EDGAR ARANA on 28/08/2017.
 */

public class Breed {

    public static List<String> breedList() {
        List<String> bl=new ArrayList<>();
       bl.add("Selecciona la Raza");
       bl.add("American Bully");
       bl.add("Alaskan");
       bl.add("Beagle");
       bl.add("Boxer");
       bl.add("Cocker");
       bl.add("Dálmata");
       bl.add("Dobermann");
       bl.add("Golden Retriever");
       bl.add("Pastor Alemán");
       bl.add("Pitbull");
       bl.add("San Bernardo");
       bl.add("Terrier");
       bl.add("Rottweiler");

        return bl;
    }
}
