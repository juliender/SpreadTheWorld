package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "app_user")
// user is reserved keyword in DB
public class User extends Model {

	@Id
	public UUID id;

    @OneToMany(mappedBy="appUser",cascade = CascadeType.ALL)
	public List<IdentityId> identityId;

	private static Finder<UUID, User> finder = new Finder<UUID, User>(
			UUID.class, User.class);

	public static User findById(final UUID id) {
		return finder.byId(id);
	}


	public static List<User> findAll() {
		return finder.all();
	}

	public static User findByIdentityId(final securesocial.core.IdentityId identityId) {
		IdentityId myIdentityId = IdentityId.findByUserIdAndProviderId(identityId.userId(), identityId.providerId());
		if(myIdentityId!=null){
			return myIdentityId.appUser;
		}
		return null;
	}


    public boolean isAdmin(){
        for(IdentityId identityId : this.identityId){
            if(identityId.providerId.equals("facebook")){
                if(identityId.userId.equals("1167645968")
                        || identityId.userId.equals("1053239123")
                        || identityId.userId.equals("824164554")

                        ){
                        return true;
                }
            }
        }
        return false;
    }
}
