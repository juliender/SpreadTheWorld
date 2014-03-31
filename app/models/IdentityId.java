package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
public class IdentityId extends Model {

	@Id
	public UUID id;

	@ManyToOne
	@JoinColumn(name="app_user_id", referencedColumnName="id")
	public User appUser;

	@Column
	public String userId;

    @Column
    public String providerId;

    @Column
    public String firstname;

    @Column
    public String lastname;

    @Column
    public String fullname;

    @Column
    public String email;


	private static Finder<UUID, IdentityId> finder = new Finder<UUID, IdentityId>(
			UUID.class, IdentityId.class);


	public static IdentityId findById(final UUID id) {
		return finder.byId(id);
	}

	public static List<IdentityId> findAll() {
		return finder.all();
	}

	public static IdentityId findByUserIdAndProviderId(final String userId, final String providerId) {
		return finder.where().eq("user_id",userId).eq("provider_id",providerId).findUnique();
	}

}
