package com.ipamc.election.services;

import com.ipamc.election.data.entity.User;
import com.ipamc.election.payload.request.SignupRequest;

public interface IUserService {
    User registerNewUserAccount(SignupRequest user);
}
