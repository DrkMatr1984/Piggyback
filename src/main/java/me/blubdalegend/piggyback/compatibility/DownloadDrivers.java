package me.blubdalegend.piggyback.compatibility;


import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.util.graph.selector.ExclusionDependencySelector;

import me.blubdalegend.piggyback.Piggyback;

import org.eclipse.aether.impl.DefaultServiceLocator;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;


@SuppressWarnings("deprecation")
public class DownloadDrivers {

    public static void downloadDriver(String storageType, File libDir) {
        try {
            // Initialize the repository system and session
            RepositorySystem system = newRepositorySystem();
            if (system == null) {
                throw new IllegalStateException("RepositorySystem is null");
            }
            RepositorySystemSession session = newRepositorySystemSession(system);

            // Define the artifact to be downloaded
            Artifact artifact = null;
            if (storageType.equalsIgnoreCase("postgre")) {
                artifact = new DefaultArtifact("org.postgresql:postgresql:LATEST");
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', Piggyback.getPlugin().lang.prefix + " &eAttempting to download latest &f" + storageType.toUpperCase() +  " &edrivers..."));
            }
            if(storageType.equalsIgnoreCase("h2")) {
                artifact = new DefaultArtifact("com.h2database:h2:LATEST");
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', Piggyback.getPlugin().lang.prefix + " &eAttempting to download latest &f" + storageType.toUpperCase() +  " &edrivers..."));
            }

            // Define the remote repository to fetch the artifact from
            RemoteRepository central = new RemoteRepository.Builder("central", "default", "https://repo.maven.apache.org/maven2/").build();

            // Create the artifact request
            if(artifact!=null) {
            	ArtifactRequest artifactRequest = new ArtifactRequest();
                artifactRequest.setArtifact(artifact);
                artifactRequest.setRepositories(Collections.singletonList(central));

                // Download the artifact
                ArtifactResult artifactResult = system.resolveArtifact(session, artifactRequest);

                // Copy the downloaded artifact to the target directory
                File targetFile = new File(libDir, artifactResult.getArtifact().getFile().getName());
                Files.copy(artifactResult.getArtifact().getFile().toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', Piggyback.getPlugin().lang.prefix + " &aDownloaded: &f" + targetFile.getName()));
            }else {
            	Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', Piggyback.getPlugin().lang.prefix + " &cAn Error occurred while attempting to download the latest"));
            	Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', Piggyback.getPlugin().lang.prefix + " &f" + storageType.toUpperCase() +  " &cdrivers..."));
            }           
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static RepositorySystem newRepositorySystem()
    {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService( RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class );
        locator.addService( TransporterFactory.class, HttpTransporterFactory.class );

        locator.setErrorHandler( new DefaultServiceLocator.ErrorHandler()
        {
            @Override
            public void serviceCreationFailed( Class<?> type, Class<?> impl, Throwable exception )
            {
                exception.printStackTrace();
            }
        } );

        return locator.getService( RepositorySystem.class );
    }

    private static RepositorySystemSession newRepositorySystemSession(RepositorySystem system) {
        DefaultRepositorySystemSession session = new DefaultRepositorySystemSession();
        LocalRepository localRepo = new LocalRepository("target/local-repo");
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));
        session.setDependencySelector(new ExclusionDependencySelector());
        return session;
    }
}