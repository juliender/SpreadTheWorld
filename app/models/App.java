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

    public App(String appName){
        this.name=appName;
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

    public static App get(String appName) {
        App app = findByName(appName);
        if(app==null){
            app = new App(appName);
            app.save();
        }
        return app;
    }
}
