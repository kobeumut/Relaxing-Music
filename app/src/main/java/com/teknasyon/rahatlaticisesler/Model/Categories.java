package com.teknasyon.rahatlaticisesler.Model;

/**
 * Created by Umut ADALI on 22.06.2018.
 */
public class Categories {

    /**
     * id : 1
     * category : Kuş Sesleri
     * image : http://sprott.physics.wisc.edu/wop/sounds/Bird%20Flapping.wav
     */

    private int id;
    private String category;
    private String image;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
