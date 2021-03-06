/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.model.dwh;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.EnableBusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;
import org.meveo.model.security.Role;

/**
 * General chart configuration
 * 
 * @author Andrius Karpavicius
 */
@Entity
@Cacheable
@ModuleItem
@ExportIdentifier({ "code" })
@Table(name = "dwh_chart", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@Inheritance(strategy = InheritanceType.JOINED)
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "dwh_chart_seq"), })
public class Chart extends EnableBusinessEntity {

    private static final long serialVersionUID = 7127515648757614672L;

    /**
     * Role allowed to see the chart
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    /**
     * Measurable quantity to display in a chart
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "msr_qty_id")
    private MeasurableQuantity measurableQuantity;

    /**
     * Chart width
     */
    @Column(name = "width", length = 10)
    @Size(max = 10)
    private String width = "500px";

    /**
     * Chart height
     */
    @Column(name = "height", length = 10)
    @Size(max = 10)
    private String height = "300px";

    /**
     * CSS Style
     */
    @Column(name = "css_style", length = 1000)
    @Size(max = 1000)
    private String style;

    /**
     * CSS style class
     */
    @Column(name = "css_style_class", length = 255)
    @Size(max = 255)
    private String styleClass;

    /**
     * Exterder
     */
    @Column(name = "extender", length = 255)
    @Size(max = 255)
    private String extender;

    /**
     * Is chart visible
     */
    @Type(type = "numeric_boolean")
    @Column(name = "visible")
    private Boolean visible = false;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public MeasurableQuantity getMeasurableQuantity() {
        return measurableQuantity;
    }

    public void setMeasurableQuantity(MeasurableQuantity measurableQuantity) {
        this.measurableQuantity = measurableQuantity;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public String getExtender() {
        return extender;
    }

    public void setExtender(String extender) {
        this.extender = extender;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean isVisible) {
        this.visible = isVisible;
    }

}
