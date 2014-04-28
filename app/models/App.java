package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
public class App extends Model {

	@Id
	public UUID id;

    @Column
    public String name;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "membership")
    public List<User> users;

    @OneToOne
    public User owner;

    @ManyToOne
    public Picture backgroundPicture;

    @ManyToOne
    public Picture middlePicture;

    @Column
    public String message;

    @Column
    public int countMessages;

    public App(String appName){
        this.name=appName;
        this.countMessages=0;
    }

	private static Finder<UUID, App> finder = new Finder<UUID, App>(
			UUID.class, App.class);

	public static App findById(final UUID id) {
		return finder.byId(id);
	}


    public static List<App> findAll() {
        return finder.all();
    }

    public static App findByName(String appName) {
        return finder.where().eq("name",appName).findUnique();
    }

    public static boolean create(String appName) {
        App app = findByName(appName);
        if(app==null){
            app = new App(appName);
            app.save();
            return true;
        }
        return false;
    }
}
