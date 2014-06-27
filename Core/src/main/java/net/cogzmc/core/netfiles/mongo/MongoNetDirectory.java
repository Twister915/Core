package net.cogzmc.core.netfiles.mongo;

import com.google.common.collect.ImmutableList;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSFile;
import com.sun.javaws.exceptions.InvalidArgumentException;
import net.cogzmc.core.netfiles.NetDirectory;
import net.cogzmc.core.netfiles.NetElement;
import net.cogzmc.core.netfiles.NetFile;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//Joe was here

/**
 *  This represents a file system directory using Mongo. This directory implements use of a GridFSFile, but does not use the File functionality. The only fields that are used from GridFSFile are the
 *  filename field, and the ID of the GridFSFile. In addition to these two fields, this class also creates a metadata object which stores child information. The test for whether a given GridFSFile is a file or
 *  if it is a directory is the existence of the metadata.children field. If that field does not exist, then the element is assumed to be a file, and not a directory. All children stored are stored as
 *  ObjectId references.
 */
class MongoNetDirectory extends MongoNetElement implements NetDirectory{

    protected MongoNetDirectory(GridFSFile me, GridFS fs) {  //Reference Object
        super(me,fs);
        if(!me.getMetaData().containsField("children")){
            me.getMetaData().put("children",new ArrayList<ObjectId>(0));
            me.save();
        }
    }

    /**
     * Returns all {@link net.cogzmc.core.netfiles.NetFile}s within this
     * @return  All subfiles of this
     */
    @Override
    public List<NetFile> getFiles() {
        List<NetFile> element = new ArrayList<>();
        List<ObjectId> children = (List<ObjectId>) getMe().getMetaData().get("children");
        for(ObjectId child : children){
            MongoNetElement equivElement = new MongoNetElement(this.getFs().find(child),getFs());
            if(!equivElement.isDirectory()){
                element.add(((MongoNetFile) equivElement));
            }
        }
        return ImmutableList.copyOf(element);
    }

    /**
     * Returns all {@link net.cogzmc.core.netfiles.NetDirectory}s within this
     * @return  All subdirectories of this
     */
    @Override
    public List<NetDirectory> getDirectories() {
        List<NetDirectory> element = new ArrayList<>();
        List<ObjectId> children = (List<ObjectId>) getMe().getMetaData().get("children");
        for(ObjectId child : children){
            MongoNetElement equivElement = new MongoNetElement(this.getFs().find(child),getFs());
            if(equivElement.isDirectory()){
                element.add(((MongoNetDirectory) equivElement));
            }
        }
        return ImmutableList.copyOf(element);
    }

    /**
     * Returns all {@link net.cogzmc.core.netfiles.NetDirectory}s and {@link net.cogzmc.core.netfiles.NetFile}s within this
     * @return  All elements within this directory
     */
    @Override
    public List<NetElement> getContents() {
        List<NetElement> element = new ArrayList<>();
        List<ObjectId> children = (List<ObjectId>) getMe().getMetaData().get("children");
        for(ObjectId child : children){
            MongoNetElement equivElement = new MongoNetElement(this.getFs().find(child),getFs());
            element.add(equivElement);
        }
        return ImmutableList.copyOf(element);
    }


    /**
     * Places a {@link net.cogzmc.core.netfiles.NetElement} within the metadata.children array, to indicate that the {@link net.cogzmc.core.netfiles.NetElement} is a child of this
     * The name CANNOT be '/', since it is reserved for Root.
     * @param element   {@link net.cogzmc.core.netfiles.NetElement} to add to the children list
     */
    private void placeElement(NetElement element){
        if(element.getName().equals("/")){ //Reserved name for root
            throw new MongoIllegalFilename();
        }

        DBObject meta = getMe().getMetaData();
        List<ObjectId> children = (List<ObjectId>) meta.get("children");
        if(children == null){
            children = new ArrayList<>(1);  //Since it doesn't exist, probably want to make a new list. Since we are a directory, this SHOULD be here, but you never know.
        }
        children.add(new ObjectId(element.getId()));
        meta.put("children",children);
        getMe().setMetaData(meta);
        getMe().save();
    }

    /**
     * Adds the NetFile to the Directory. This will add the Id of the Netfile to the metadata.children list, for it to be identified with this directory.
     * @param file  Netfile to add the ID of
     */
    @Override
    public void placeFile(NetFile file) {
        placeElement(file);
    }

    /**
     * Returns the whether the ID of the NetFile is existent within the children list.
     * @param file  NetFile to check for the existance of
     * @return
     */
    @Override
    public boolean containsFile(NetFile file) {
        String id = file.getId();
        for (ObjectId children : ((List<ObjectId>) getMe().getMetaData().get("children"))) {
            if(children.toString().equals(id)){
                return true;
            }
        }
        return false;
    }


    /**
     * Will create a {@link net.cogzmc.core.netfiles.NetDirectory} within this directory
     * @param name  Name of the directory to create
     */
    @Override
    public MongoNetDirectory createNewDirectory(String name) {
        GridFSFile createdDir = getFs().createFile(name);
        MongoNetDirectory dir = new MongoNetDirectory(createdDir,getFs());
        this.placeElement(dir);
        return dir;
    }

    /**
     * Returns an iterator of all of the children objects of this directory
     * @return  Iterator containing all of the children of this database
     */
    @Override
    public Iterator<NetElement> iterator() {
        List<ObjectId> children = ((List<ObjectId>) getMe().getMetaData().get("children"));
        return new MongoChildrenIterator(children.iterator(),getFs());
    }

}
