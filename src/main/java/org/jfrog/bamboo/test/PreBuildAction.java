package org.jfrog.bamboo.test;

import com.atlassian.bamboo.build.BuildLoggerManager;
import com.atlassian.bamboo.build.CustomPreBuildAction;
import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.repository.Repository;
import com.atlassian.bamboo.repository.RepositoryDefinition;
import com.atlassian.bamboo.security.StringEncrypter;
import com.atlassian.bamboo.v2.build.BaseConfigurableBuildPlugin;
import com.atlassian.bamboo.v2.build.BuildContext;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * @author Noam Y. Tenne
 */
public class PreBuildAction extends BaseConfigurableBuildPlugin implements CustomPreBuildAction {

    private BuildLoggerManager buildLoggerManager;

    @NotNull
    public BuildContext call() throws InterruptedException, Exception {
        Iterator<RepositoryDefinition> repoDefIterator = buildContext.getRepositoryDefinitions().iterator();
        if (repoDefIterator.hasNext()) {
            RepositoryDefinition repoDef = repoDefIterator.next();
            Repository repository = repoDef.getRepository();
            HierarchicalConfiguration repoConfiguration = repository.toConfiguration();
            BuildLogger logger = buildLoggerManager.getBuildLogger(buildContext.getPlanResultKey());
            logger.addErrorLogEntry("######### Value of 'repository.github.username':" +
                    repoConfiguration.getString("repository.github.username"));
            StringEncrypter encrypter = new StringEncrypter();
            logger.addErrorLogEntry("######### Value of 'repository.github.password':" +
                    encrypter.decrypt(repoConfiguration.getString("repository.github.password")));
        }
        return buildContext;
    }

    public void setBuildLoggerManager(BuildLoggerManager buildLoggerManager) {
        this.buildLoggerManager = buildLoggerManager;
    }
}
