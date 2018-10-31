package de.dietzm.blueblue.saptoolbox;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.HashMap;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobListOption;
import com.google.cloud.storage.Storage.BucketField;
import com.google.cloud.storage.Storage.BucketGetOption;
import com.google.cloud.storage.StorageOptions;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;

public class DestinationManager {

	private static DestinationManager instance;

	private DestinationManager() {

	}

	public static DestinationManager getInstance() {
		if (instance == null) {
			instance = new DestinationManager();
		}
		return instance;
	}

	private static final String DESTINATION_FOLDER = "destinations";
	private static final String LOCAL_FOLDER = DESTINATION_FOLDER + File.separator;
	private static final String STORAGE_FOLDER = DESTINATION_FOLDER + "/";
	private static final String BUCKET_NAME_ENV = "GCS_STORAGE_BUCKET";

	public void syncDestinationsList() throws Exception {

		if (!new File(LOCAL_FOLDER).exists())
			new File(LOCAL_FOLDER).mkdirs();

		String bucketName = System.getenv(BUCKET_NAME_ENV);
		if (bucketName == null || bucketName.equals("")) {
			return;
		}

		// Get Destinations from Storage
		Storage storage = StorageOptions.getDefaultInstance().getService();
		Bucket bucket = storage.get(bucketName, BucketGetOption.fields(BucketField.ID, BucketField.NAME));
		HashMap<String, Boolean> serverFiles = new HashMap<String, Boolean>();

		for (Blob blob : bucket.list(BlobListOption.prefix(STORAGE_FOLDER), BlobListOption.currentDirectory())
				.iterateAll()) {
			if (!blob.isDirectory() && !blob.getName().endsWith("/")) {
				String fileName = blob.getName().replaceFirst(STORAGE_FOLDER, "");
				File localFile = new File(LOCAL_FOLDER + fileName);

				if (localFile.exists() && (localFile.lastModified() / 1000) == (blob.getUpdateTime() / 1000)) {
					// Nothing to do
				} else if (localFile.exists() && (localFile.lastModified() / 1000) > (blob.getUpdateTime() / 1000)) {
					serverFiles.put(fileName, false);
				} else {
					File fileToCreate = new File(LOCAL_FOLDER + fileName);
					blob.downloadTo(Paths.get(fileToCreate.getPath()));
					fileToCreate.setLastModified(blob.getUpdateTime());
					serverFiles.put(fileName, true);
				}
			}
		}

		File[] files = new File(LOCAL_FOLDER).listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (pathname.getName().endsWith(".jcoDestination"))
					return true;
				return false;
			}
		});

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				String fileName = files[i].getName();
				if (!serverFiles.containsKey(fileName)) {
					File fileToUpload = new File(LOCAL_FOLDER + fileName);
					FileInputStream fis = new FileInputStream(fileToUpload);
					Blob blob = bucket.create(STORAGE_FOLDER + fileName, fis);
					fileToUpload.setLastModified(blob.getUpdateTime());
					fis.close();
				} else if (serverFiles.get(fileName) == false) {
					File fileToUpload = new File(LOCAL_FOLDER + fileName);
					FileInputStream fis = new FileInputStream(fileToUpload);
					Blob blob = bucket.create(STORAGE_FOLDER + fileName, fis);
					fileToUpload.setLastModified(blob.getUpdateTime());
					fis.close();
				}
			}
		}

	}

	public boolean isDestinationExisting(String destname) throws Exception {
		if(new File(LOCAL_FOLDER + destname).exists()) {
			return true;
		}
		return false;
	}
	
	public JCoDestination getDestination(String destname) throws Exception {
		this.syncDestinationsList();
		return JCoDestinationManager.getDestination(LOCAL_FOLDER + destname);
	}

}
