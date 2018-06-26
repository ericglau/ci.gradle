/*
 * (C) Copyright IBM Corporation 2018.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.wasdev.wlp.gradle.plugins

import static junit.framework.Assert.assertTrue
import static org.junit.Assert.*

import org.junit.After
import org.junit.BeforeClass
import org.junit.Test

import net.wasdev.wlp.common.plugins.util.InstallFeatureUtil

class OpenLibertyInstallFeatureTest extends AbstractIntegrationTest{
    static File resourceDir = new File("build/resources/integrationTest/openliberty-install-feature-test")
    static File buildDir = new File(integTestDir, "/openliberty-install-feature-test")
    static String buildFilename = "build.gradle"

    @BeforeClass
    public static void setup() {
        createDir(buildDir)
        createTestProject(buildDir, resourceDir, buildFilename)
        try {
            runTasks(buildDir, "installLiberty", "overwriteServer", "libertyPackage")
            deleteDir(new File(buildDir, "build/wlp"));
        } catch (Exception e) {
            throw new AssertionError ("Failed to package Open Liberty kernel. "+ e)
        }
    }
    
    @After
    public static void tearDown() {
        deleteDir(new File(buildDir, "build/wlp"));
    }
    
    @Test
    public void test_installFeature_dependency() {
        copyBuildFiles(new File(resourceDir, "install_feature_dependency.gradle"), buildDir)
        try {
            runTasks(buildDir, 'installFeature')

            assertInstalled("a-1.0")
        } catch (Exception e) {
            throw new AssertionError ("Fail on task installFeature. "+e)
        }
    }

    protected void assertInstalled(String feature) throws Exception {
        assertTrue("Feature " + feature + " was not installed into the lib/features directory", existsInFeaturesDirectory(feature));
        String featureInfo = getFeatureInfo();
        assertTrue("Feature " + feature + " was not installed according to productInfo featureInfo: " + featureInfo, featureInfo.contains(feature));
    }
    
    protected boolean existsInFeaturesDirectory(String feature) {
        File[] features;
        File featuresDir = new File(buildDir, "build/wlp/lib/features")

        features = featuresDir.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.toLowerCase().endsWith("." + feature + ".mf");
                    }
                });

        return features.size() >= 1;
    }
    
    protected String getFeatureInfo() throws Exception {
        File installDirectory = new File(buildDir, "build/wlp")
        return InstallFeatureUtil.productInfo(installDirectory, "featureInfo");
    }

}
