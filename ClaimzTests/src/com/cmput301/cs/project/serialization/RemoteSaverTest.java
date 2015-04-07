package com.cmput301.cs.project.serialization;/*
 * Copyright 2015
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import com.cmput301.cs.project.models.Claim;
import com.cmput301.cs.project.models.User;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.ArrayList;

import com.cmput301.cs.project.serialization.elasticsearch.SearchHit;
import com.google.gson.reflect.TypeToken;
import junit.framework.TestCase;


public class RemoteSaverTest extends TestCase {

    public void testSaveRemote() throws IOException {


        Claim claim1 = new Claim.Builder(new User("test")).build();
        Claim claim2 = new Claim.Builder(new User("test")).build();
        Claim claim3 = new Claim.Builder(new User("test")).build();
        List<Claim> claims = new ArrayList<Claim>();
        claims.add(claim1);
        claims.add(claim2);
        claims.add(claim3);

        Type type = new TypeToken<SearchHit<Claim>>(){}.getType();

        RemoteSaver<Claim> remoteSaver = new RemoteSaver<Claim>("test", type);

        try {
            remoteSaver.saveAll(claims);
        } catch (IOException e) {
            fail();
        }

        try {
            wait(5000);
        } catch (InterruptedException e) {
            fail();
        }

        List<Claim> loadedClaims = remoteSaver.readAll();

        assertEquals("Claims are equal", loadedClaims, claims);

    }
}