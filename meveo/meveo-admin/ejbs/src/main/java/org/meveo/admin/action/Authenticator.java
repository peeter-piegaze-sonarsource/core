/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.admin.action;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.jboss.seam.security.BaseAuthenticator;
import org.jboss.seam.security.Credentials;
import org.meveo.admin.exception.InactiveUserException;
import org.meveo.admin.exception.LoginException;
import org.meveo.admin.exception.NoRoleException;
import org.meveo.admin.exception.PasswordExpiredException;
import org.meveo.admin.exception.UnknownUserException;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.security.Role;
import org.meveo.security.MeveoUser;
import org.meveo.service.admin.impl.UserService;
import org.picketlink.idm.impl.api.PasswordCredential;
import org.slf4j.Logger;

@Named
public class Authenticator extends BaseAuthenticator {

    @Inject
    private UserService userService;

    @Inject
    Credentials credentials;

    @Inject
    protected Logger log;

    // @Produces
    // @Named("currentProvider")
    private Provider currentProvider;
    //
    // @Produces
    // @Named("homeMessage")
    private String homeMessage;

    @Inject
    private Messages messages;

    // @Inject
    // TODO: private LocaleSelector localeSelector;

    /* Authentication errors */
    private boolean noLoginError;
    private boolean inactiveUserError;
    private boolean noRoleError;
    private boolean passwordExpired;

    // public User internalAuthenticate(Principal principal, List<String> roles) {
    //
    // User user = null;
    //
    // try {
    // user = userService.loginChecks("meveo.admin", null);
    //
    // } catch (LoginException e) {
    // log.info("Login failed for the user #" + user.getId(), e);
    // if (e instanceof InactiveUserException) {
    // inactiveUserError = true;
    //
    // } else if (e instanceof NoRoleException) {
    // noRoleError = true;
    //
    // } else if (e instanceof PasswordExpiredException) {
    // passwordExpired = true;
    //
    // } else if (e instanceof UnknownUserException) {
    // noLoginError = true;
    // }
    // }
    //
    // homeMessage = "application.home.message";
    //
    // if (user == null) {
    // setStatus(AuthenticationStatus.FAILURE);
    // } else {
    //
    // homeMessage = "application.home.message";
    //
    // setStatus(AuthenticationStatus.SUCCESS);
    // setUser(new MeveoUser(user));
    //
    // if (user.isOnlyOneProvider()) {
    // currentProvider = user.getProviders().get(0);
    // }
    //
    // // TODO needed to overcome lazy loading issue. Remove once solved
    // for (Role role : user.getRoles()) {
    // for (org.meveo.model.security.Permission permission : role.getPermissions()) {
    // permission.getName();
    // }
    // }
    // }
    // return user;
    // }

    public String localLogout() {
        // TODO: Identity.instance().logout();
        return "loggedOut";
    }

    public void authenticate() {

        noLoginError = false;
        inactiveUserError = false;
        noRoleError = false;
        passwordExpired = false;

        User user = null;
        try {

            /* Authentication check */
            user = userService.loginChecks(credentials.getUsername(), ((PasswordCredential) credentials.getCredential()).getValue());

        } catch (LoginException e) {
            log.info("Login failed for the user {0} for reason {1} {2}" + credentials.getUsername(), e.getClass().getName(), e.getMessage());
            if (e instanceof InactiveUserException) {
                inactiveUserError = true;
                log.error("login failed with username=" + credentials.getUsername() + " and password=" + ((PasswordCredential) credentials.getCredential()).getValue()
                        + " : cause user is not active");
                messages.info(new BundleKey("messages", "user.error.inactive"));

            } else if (e instanceof NoRoleException) {
                noRoleError = true;
                log.error("The password of user " + credentials.getUsername() + " has expired.");
                messages.info(new BundleKey("messages", "user.error.noRole"));

            } else if (e instanceof PasswordExpiredException) {
                passwordExpired = true;
                log.error("The password of user " + credentials.getUsername() + " has expired.");
                messages.info(new BundleKey("messages", "user.password.expired"));

            } else if (e instanceof UnknownUserException) {
                noLoginError = true;
                log.info("login failed with username={0} and password={1}", credentials.getUsername(), ((PasswordCredential) credentials.getCredential()).getValue());
                messages.info(new BundleKey("messages", "user.error.login"));
            }
        }

        if (user == null) {
            setStatus(AuthenticationStatus.FAILURE);
        } else {

            homeMessage = "application.home.message";

            setStatus(AuthenticationStatus.SUCCESS);
            setUser(new MeveoUser(user));

            if (user.isOnlyOneProvider()) {
                currentProvider = user.getProviders().get(0);
            }

            // TODO needed to overcome lazy loading issue. Remove once solved
            for (Role role : user.getRoles()) {
                for (org.meveo.model.security.Permission permission : role.getPermissions()) {
                    permission.getName();
                }
            }
            log.info("End of authenticating");
        }
    }

    public void setLocale(String language) {
        // TODO: localeSelector.selectLanguage(language);

    }
}