package com.mapyo.everydayredbook.app;

public class RedData {
    private String category;
    private String taxon;
    private String japaneseName;
    private String scientificName;

    public RedData( ) {
    }

    public void setRedData(
            String category,
            String taxon,
            String japaneseName,
            String scientific_name
    ) {
        // todo:いずれ必要そうだったら、各自のsetterを作る
        this.category = category;
        this.taxon = taxon;
        this.japaneseName = japaneseName;
        this.scientificName = scientific_name;
    }

    public String getCategory() {
        return this.category;
    }

    public String getTaxon() {
       return this.taxon;
    }

    public String getJapaneseName(){
        return this.japaneseName;
    }

    public String getScientificName() {
        return this.scientificName;
    }
}
