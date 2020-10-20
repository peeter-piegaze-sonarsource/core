package org.meveo.model.article;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

import org.hibernate.annotations.GenericGenerator;
import org.meveo.model.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "billing_article_mapping_line")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = { @org.hibernate.annotations.Parameter(name = "sequence_name", value = "billing_article_mapping_line_seq"), })
public class ArticleMappingLine extends BaseEntity {

    @OneToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "article_mapping_id")
    private ArticleMapping articleMapping;

    @OneToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "article_id")
    private Article article;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "service_code")
    private String serviceCode;

    @Column(name = "charge_code")
    private String chargeCode;

    @Column(name = "parameter_1")
    private String parameter1;

    @Column(name = "parameter_2")
    private String parameter2;

    @Column(name = "parameter_3")
    private String parameter3;

    public ArticleMapping getArticleMapping() {
        return articleMapping;
    }

    public void setArticleMapping(ArticleMapping articleMapping) {
        this.articleMapping = articleMapping;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getChargeCode() {
        return chargeCode;
    }

    public void setChargeCode(String chargeCode) {
        this.chargeCode = chargeCode;
    }

    public String getParameter1() {
        return parameter1;
    }

    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    public String getParameter2() {
        return parameter2;
    }

    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    public String getParameter3() {
        return parameter3;
    }

    public void setParameter3(String parameter3) {
        this.parameter3 = parameter3;
    }
}
