package com.teknasyon.rahatlaticisesler.Model;

/**
 * Created by Umut ADALI on 25.06.2018.
 */
public class Event {
    public String message;
    public String where;
    public boolean status;
    public Event(){ //EventBusta boş halde çağırıp silebilmek için eklendi.

    }
    public Event(String message) {
        this.message = message;
    }
    public Event(String where, String message) {
        this.message = message;
        this.where = where;
    }
    public Event(boolean status) {
        this.status = status;
    }

}
