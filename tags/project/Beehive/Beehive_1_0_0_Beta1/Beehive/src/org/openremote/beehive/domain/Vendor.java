package org.openremote.beehive.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * This is the top level hierarchy shown in http://lirc.sourceforge.net/remotes/. Such as Sony, Apple, Samsung etc.
 *
 * @author Dan 2009-2-6
 */
@Entity
@SuppressWarnings("serial")
@Table(name = "vendor")
public class Vendor extends BusinessEntity {

    private String name;
    private List<Model> models;

    public Vendor() {
        models = new ArrayList<Model>();
    }

    @Column(nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    public List<Model> getModels() {
        return models;
    }

    public void setModels(List<Model> models) {
        this.models = models;
    }

}
