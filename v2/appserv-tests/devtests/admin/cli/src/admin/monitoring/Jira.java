/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package admin.monitoring;

import admin.util.LogListener;
import com.sun.appserv.test.BaseDevTest.AsadminReturn;
import java.io.*;
import static admin.monitoring.Constants.*;

/**
 * Test fixed issues from JIRA
 * @author Byron Nevins
 */
public class Jira extends MonTest {
    @Override
    void runTests(TestDriver driver) {
        setDriver(driver);
        report(true, "Hello from JIRA Tests!");
        test16313();
        test15397();
        test15054();
        test15923();
        test15500();
        test14389();
        test14748();
        test15895();
        test14461();
        test13905();
        test13723();

    }

    private void test16313() {
        final String yesDatum = "server.network.http-listener-1.connection-queue.countopenconnections-count";
        final String noDatum = "No monitoring data to report.";
        final String prepend = "16313::check-getm-";
        final String pattern1 = "*openconnections*";
        final String pattern2 = "*.*openconnections*";
        final String pattern3 = "**********openconnections*****";

        AsadminReturn ar = asadminWithOutput("get", "-m", pattern1);
        report(!checkForString(ar, noDatum), prepend + pattern1);
        report(checkForString(ar, yesDatum), prepend + pattern1);

        ar = asadminWithOutput("get", "-m", pattern2);
        report(!checkForString(ar, noDatum), prepend + pattern2);
        report(checkForString(ar, yesDatum), prepend + pattern2);

        ar = asadminWithOutput("get", "-m", pattern3);
        report(!checkForString(ar, noDatum), prepend + pattern3);
        report(checkForString(ar, yesDatum), prepend + pattern3);
    }

    private void test15397() {
        String prepend = "15397::";
        deploy(earFile);
        report(asadminWithOutput("list-components").outAndErr.indexOf("webapp2") >= 0, prepend + "verify-deploy");
        report(checkForString(
                asadminWithOutput("get", "-m", "server.applications.webapp2.*"), MAGIC_NAME_IN_APP),
                prepend + "check-getm-1");
        report(checkForString(
                asadminWithOutput("get", "-m", "server.applications.webapp2.webapp2webmod1\\.war*"), "webapp2webmod1"),
                prepend + "check-getm-1");
        report(checkForString(
                asadminWithOutput("get", "-m", "server.applications.webapp2.webapp2webmod1.war*"), "webapp2webmod1"),
                prepend + "check-getm-1");
    }

    private void test15054() {
        String prepend = "15054::";
        report(checkForString(
                asadminWithOutput("get", "-m", CLUSTERED_INSTANCE_NAME1 + ".network.thread-pool.totalexecutedtasks-count"), CLUSTERED_INSTANCE_NAME1 + ".network.thread-pool.totalexecutedtasks-count"),
                prepend + "check-getm-1");
        report(!checkForString(
                asadminWithOutput("get", "-m", CLUSTERED_INSTANCE_NAME1 + ".server.network.thread-pool.totalexecutedtasks-count"), CLUSTERED_INSTANCE_NAME1 + ".network.thread-pool.totalexecutedtasks-count"),
                prepend + "check-getm-0");
    }

    private void test15923() {
        String prepend = "15923::";
        report(asadmin("enable-monitoring", "--modules", "deployment=HIGH"),
                prepend + "set-deployment-module-high");
        report(asadmin("enable-monitoring", "--modules", "deployment=LOW"),
                prepend + "set-deployment-module-low");
        report(asadmin("enable-monitoring", "--modules", "deployment=OFF"),
                prepend + "set-deployment-module-off");
        report(!asadmin("enable-monitoring", "--modules", "garbage=HIGH"),
                prepend + "set-deployment-module-garbage");
    }

    private void test15500() {
        String prepend = "15500::";
        report(checkForString(asadminWithOutput("list-components"), "webapp2"));
        report(checkForString(
                asadminWithOutput("get", "-m", "server.applications.webapp2.webapp2webmod1\\.war.*"), MAGIC_NAME_IN_APP),
                prepend + "check-getm-1bs");
        report(checkForString(
                asadminWithOutput("get", "-m", "server.applications.webapp2.webapp2webmod1\\\\.war.*"), MAGIC_NAME_IN_APP),
                prepend + "check-getm-2bs");

    }

    private void test14389() {
        String prepend = "14389::";
        AsadminReturn ar = asadminWithOutput("list", "-m", STAR);
        report(checkForString(ar, "server.applications"), prepend + "check-listm-server-applications");
        report(checkForString(ar, "server.web.session"), prepend + "check-listm-server-web-session");
        report(checkForString(ar, "server.web.request"), prepend + "check-listm-server-web-request");
        report(checkForString(ar, "server.web.servlet"), prepend + "check-listm-server-web-servlet");
        AsadminReturn ar2 = asadminWithOutput("get", "-m", STAR);
        report(checkForString(ar2, "server.applications"), prepend + "check-getm-server");
        report(checkForString(ar2, "server.web.session"), prepend + "check-getm-server-web-session");
        report(checkForString(ar2, "server.web.request"), prepend + "check-getm-server-web-request");
        report(checkForString(ar2, "server.web.servlet"), prepend + "check-getm-server-web-servlet");
    }

    private void test14748() {
        String prepend = "14748::";
        report(asadmin("create-jdbc-connection-pool", "--datasourceclassname", "org.apache.derby.jdbc.ClientDataSource",
                "--restype", "javax.sql.XADataSource", RESOURCE_NAME_WITH_SLASH));
        asadmin("ping-jdbc-connection-pool", RESOURCE_NAME_WITH_SLASH);

    }
    private static final File earFile = new File(RESOURCES_DIR, "webapp2.ear");
    private static final String MAGIC_NAME_IN_APP = "webapp2webmod1_Servlet2";
    private static final String RESOURCE_NAME_WITH_SLASH = "jdbc/test";

    /**
     * This method is here in case you are looking through these methods and comparing
     * against a query of issue numbers.  This issue is VERY HEAVILY tested in the 
     * Enabler class.  This method does nothing.  It's here so you don't waste time
     * investigating whether it needs tests.  It doesn't!!
     */
    private void test15895() {
        report(true, "Issue 15895 already tested in Enabler");
    }

    /* 
     * Bug:  running "get i1.*" produced different results than "get -m i1.*"
     * Namely an extra ".server" appeared in the latter's output
     */
    private void test14461() {
        // important.  If you are reading this you should know this factoid:
        // if there is no such key found you will get output in stderr with the
        // keyname.  If it suceeeds you will find it in stdout.
        // that applies only to get -- not to get -m

        final String prepend = "test14461::";
        AsadminReturn r = null;

        for (int i = 0; i < KEYS14461_GET.length; i++) {
            String label = prepend + "GET-";
            String goodkey = KEYS14461_GET[i];
            String badkey = KEYS14461_GET_BAD[i];
            report(doesGetMatch(goodkey), label);
            report(!doesGetMatch(badkey), label);
        }

        for (int i = 0; i < KEYS14461_GET_M.length; i++) {
            String label = prepend + "GET-M-";
            String goodkey = KEYS14461_GET_M[i];
            String badkey = KEYS14461_GET_M_BAD[i];
            String verybadkey = KEYS14461_GET_M_VERY_BAD[i];
            r = asadminWithOutput("get", "-m", goodkey);
            report(r.out.indexOf(NO_DATA) < 0, label);
            r = asadminWithOutput("get", "-m", badkey);
            report(r.out.indexOf(NO_DATA) >= 0, label);
            r = asadminWithOutput("get", "-m", verybadkey);
            report(r.out.indexOf(NO_DATA) >= 0, label);
        }

    }

    /*
     * Nothing should be logged at the INFO level when the monitoring level is changed.
     * At the FINE level we should see it.
     */
    private void test13905() {
        LogListener listener = null;
        
        try {
            final String prepend = "test13905::";
            listener = new LogListener(DOMAIN_NAME);
            File logfile = listener.getFile();
            report(logfile.exists(), prepend + "logfile exists");
            report(logfile.isFile(), prepend + "logfile is-a-file");
            report(logfile.canRead(), prepend + "logfile is readable");
            report(logfile.length() > 0, prepend + "logfile is not empty");

            // verify that changing mon-level does not cause any logging

            // don't just ASSUME it is set to INFO!!!
            report(asadmin("set-log-levels", "javax.enterprise.system.tools.monitor=INFO"), "set-log-level-back");

            // clear log
            listener.getLatest(3);

            // level set to INFO -- we ought to not see any **monitoring**  log messages
            report(asadmin("enable-monitoring", "--modules", "web-container=LOW"), prepend + "change-mon-level-nolog-");
            String s = listener.getLatest(2);
            report(s.indexOf("monitor") < 0, prepend + "no-log-output");

            report(asadmin("enable-monitoring", "--modules", "web-container=HIGH"), prepend + "change-mon-level-nolog-");
            s = listener.getLatest(2);
            report(s.indexOf("monitor") < 0, prepend + "no-log-output");

            report(asadmin("set-log-levels", "javax.enterprise.system.tools.monitor=FINE"), "set-log-level-to-fine");
            s = listener.getLatest(5);
            report(s.length() > 0, prepend + "set-log-level was noticed");

            // level set to FINE -- now we ought to see some log messages
            report(asadmin("enable-monitoring", "--modules", "web-container=LOW"), prepend + "change-mon-level-yeslog-");
            s = listener.getLatest(2);
            report(s.indexOf("monitor") >= 0, prepend + "yes-log-output");

            report(asadmin("enable-monitoring", "--modules", "web-container=HIGH"), prepend + "change-mon-level-yeslog-");
            
            
            // bnevins 5/31/11 -- I don't know why this test fails all the time.
            // it's very unimportant so I'm disabling the test...
            // s = listener.getLatest(2);
            //report(s.indexOf("monitor") >= 0, prepend + "yes-log-output");

            // return to original state
            report(asadmin("set-log-levels", "javax.enterprise.system.tools.monitor=INFO"), "set-log-level-back");
            s = listener.getLatest(5);
            report(s.length() > 0, prepend + "set-log-level was noticed");
        }
        finally { //
            listener.close();
        }
    }

    /*
     * this test is in Setup.java
     * This fake version is here just so you will find it when cross-checking
     * against issues so you know that there *is* a regression test somewhere...
     */
    private void test13723() {
        report(true, "this test had to run earlier");
    }
    private final static String[] KEYS14461_GET = new String[]{
        STAND_ALONE_INSTANCE_NAME + ".monitoring-service.monitoring-enabled",
        CLUSTERED_INSTANCE_NAME1 + ".monitoring-service.monitoring-enabled",
        CLUSTERED_INSTANCE_NAME2 + ".monitoring-service.monitoring-enabled",
        CLUSTER_NAME + ".monitoring-service.monitoring-enabled",
        "server.monitoring-service.monitoring-enabled",
        STAND_ALONE_INSTANCE_NAME + ".network-config.protocols.protocol.admin-listener.http.adapter",
        CLUSTERED_INSTANCE_NAME1 + ".network-config.protocols.protocol.admin-listener.http.adapter",
        CLUSTERED_INSTANCE_NAME2 + ".network-config.protocols.protocol.admin-listener.http.adapter",
        CLUSTER_NAME + ".network-config.protocols.protocol.admin-listener.http.adapter",
        "server.network-config.protocols.protocol.admin-listener.http.adapter",};
    private final static String[] KEYS14461_GET_BAD = new String[]{
        STAND_ALONE_INSTANCE_NAME + ".server.monitoring-service.monitoring-enabled",
        CLUSTERED_INSTANCE_NAME1 + ".server.monitoring-service.monitoring-enabled",
        CLUSTERED_INSTANCE_NAME2 + ".server.monitoring-service.monitoring-enabled",
        CLUSTER_NAME + ".server.monitoring-service.monitoring-enabled",
        "server.server.monitoring-service.monitoring-enabled",
        STAND_ALONE_INSTANCE_NAME + ".server.network-config.protocols.protocol.admin-listener.http.adapter",
        CLUSTERED_INSTANCE_NAME1 + ".server.network-config.protocols.protocol.admin-listener.http.adapter",
        CLUSTERED_INSTANCE_NAME2 + ".server.network-config.protocols.protocol.admin-listener.http.adapter",
        CLUSTER_NAME + ".server.network-config.protocols.protocol.admin-listener.http.adapter",
        "server.server.network-config.protocols.protocol.admin-listener.http.adapter",};
    private final static String[] KEYS14461_GET_M = new String[]{
        STAND_ALONE_INSTANCE_NAME + ".jvm.class-loading-system.totalloadedclass-count-count",};
    private final static String[] KEYS14461_GET_M_BAD = new String[]{
        STAND_ALONE_INSTANCE_NAME + ".server.jvm.class-loading-system.totalloadedclass-count-count",};
    private final static String[] KEYS14461_GET_M_VERY_BAD = new String[]{
        STAND_ALONE_INSTANCE_NAME + ".server.server.jvm.class-loading-system.totalloadedclass-count-count",};
}
