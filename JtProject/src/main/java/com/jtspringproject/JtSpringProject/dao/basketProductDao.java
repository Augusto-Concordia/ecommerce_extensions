package com.jtspringproject.JtSpringProject.dao;
import com.jtspringproject.JtSpringProject.models.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.hibernate.query.Query;

import java.util.List;

@Repository
public class basketProductDao {
    @Autowired
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sf) {
        this.sessionFactory = sf;
    }

    public void save(BasketProduct basketProduct) {
        Session session = sessionFactory.getCurrentSession();
        session.persist(basketProduct);
    }

    public void update(BasketProduct basketProduct) {
        Session session = sessionFactory.getCurrentSession();
        session.update(basketProduct);
    }


    public void delete(int id) {
        Session session = sessionFactory.getCurrentSession();
        BasketProduct basket_product = session.get(BasketProduct.class, id);
        if (basket_product != null) {
            session.delete(basket_product);
        }
    }

    public BasketProduct findById(int id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(BasketProduct.class, id);
    }

    @SuppressWarnings("unchecked")
    public List<BasketProduct> findAll() {
        return this.sessionFactory.getCurrentSession().createQuery("from BASKET_PRODUCT").list();
    }

    public void deleteAll() {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("delete from BASKET_PRODUCT");
        query.executeUpdate();
    }

}