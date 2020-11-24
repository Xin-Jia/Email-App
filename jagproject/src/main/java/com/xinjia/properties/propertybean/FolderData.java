package com.xinjia.properties.propertybean;

import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * JavaFX Folder bean that contains an id and a folder name. Overrides toString,
 * hashCode and equals
 *
 * @author Xin Jia Cao
 */
public class FolderData {

    private IntegerProperty id;
    private StringProperty folderName;

    /**
     * Non-default constructor that initializes the id and the folder name
     *
     * @param id
     * @param folderName
     */
    public FolderData(final int id, final String folderName) {
        this.id = new SimpleIntegerProperty(id);
        this.folderName = new SimpleStringProperty(folderName);
    }

    /**
     * Default constructor
     */
    public FolderData() {
        this(-1, "");
    }

    public int getId() {
        return id.get();
    }

    public void setId(final int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getFolderName() {
        return folderName.get();
    }

    public void setFolderName(final String folderName) {
        this.folderName.set(folderName);
    }

    public StringProperty folderNameProperty() {
        return folderName;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.id);
        hash = 89 * hash + Objects.hashCode(this.folderName);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FolderData other = (FolderData) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.folderName, other.folderName)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "FolderData{" + "id=" + id + ", folderName=" + folderName + '}';
    }

}
