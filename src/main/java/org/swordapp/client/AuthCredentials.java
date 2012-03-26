/*
 * Copyright (c) 2011, Richard Jones, Cottage Labs
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.swordapp.client;

/**
 * Entity class representing the Authentication Credentials which can be used by
 * SWORD to do HTTP Basic authentication and also to supply the On-Behalf-Of
 * header
 */
public class AuthCredentials
{
    private String username;
    private String password;
    private String onBehalfOf;
    private String target = null;
    private String realm = null;

    public AuthCredentials(String username, String password, String onBehalfOf, String target, String realm)
    {
        this.username = username;
        this.password = password;
        this.onBehalfOf = onBehalfOf;
        this.target = target;
        this.realm = realm;
    }

    public AuthCredentials(String username, String password, String target, String realm)
    {
        this.username = username;
        this.password = password;
        this.target = target;
        this.realm = realm;
    }

    public AuthCredentials(String username, String password, String onBehalfOf)
    {
        this.username = username;
        this.password = password;
        this.onBehalfOf = onBehalfOf;
    }

    public AuthCredentials(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

    public AuthCredentials(String onBehalfOf)
    {
        this.onBehalfOf = onBehalfOf;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public String getOnBehalfOf()
    {
        return onBehalfOf;
    }

    public String getTarget()
    {
        return target;
    }

    public String getRealm()
    {
        return realm;
    }
}
