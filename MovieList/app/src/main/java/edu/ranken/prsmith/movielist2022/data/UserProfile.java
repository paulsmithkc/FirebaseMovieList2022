package edu.ranken.prsmith.movielist2022.data;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UserProfile {
    @DocumentId
    public String userId;
    public String displayName;
    public String photoUrl;
    @ServerTimestamp
    public Date lastLogin;

    // empty constructor used for serialization
    public UserProfile() {}
}
