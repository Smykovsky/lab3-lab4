package pl.smyk.lab3.dao;

import javafx.scene.control.Alert;
import org.hibernate.*;
import pl.smyk.lab3.model.Code;
import pl.smyk.lab3.utils.HibernateUtil;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class CodeDAO {
    private SessionFactory sessionFactory;

    public CodeDAO() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    public void loadData() {
        Session session = null;
        String record = null;
        if (!getAll().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("WARNING");
            alert.setContentText("Data can not be entered into database because already has data entered!");
            alert.show();
        } else {
            try {
                BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\kamil.smyk\\Pulpit\\kody.csv"));
                reader.readLine();
                session = HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                while ((record = reader.readLine()) != null) {
                    String[] data = record.split(";");
                    Code code = new Code();
                    code.setPostCode(data[0]);
                    code.setAdress(data[1]);
                    code.setPlace(data[2]);
                    code.setVoivoship(data[3]);
                    code.setCounty(data[4]);
                    session.save(code);

                }
                session.getTransaction().commit();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public List<Code> getAll() {
        try {
            Session session = sessionFactory.openSession();
            return session.createQuery("from Code", Code.class).list();
        } catch (HibernateException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Code> getByCriteria(String postCode, String address, String place, String voivoship, String county) {
        Session session = sessionFactory.openSession();
        String hql = "FROM Code e where e.postCode LIKE :postCode AND e.adress LIKE :adress AND e.place LIKE :place AND e.voivoship LIKE :voivoship AND e.county LIKE :county";
        Query<Code> query = session.createQuery(hql, Code.class);
        query.setParameter("postCode", "%" + postCode + "%");
        query.setParameter("adress", "%" + address + "%");
        query.setParameter("place", "%" + place + "%");
        query.setParameter("voivoship", "%" + voivoship + "%");
        query.setParameter("county", "%" + county + "%");

        List<Code> result = query.getResultList();
        return result;
    }

    public List<Code>getByPlace(String place) {
        Session session = sessionFactory.openSession();
        String hql = "FROM Code e where e.place LIKE :place";
        Query<Code> query = session.createQuery(hql, Code.class);
        query.setParameter("place","%" + place + "%");

        List<Code> result = query.getResultList();
        return result;
    }

    public void delete(Long id) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        try {
            transaction = session.beginTransaction();
            Code entityToDelete = session.get(Code.class, id);
            session.delete(entityToDelete);
            transaction.commit();

            alert.setTitle("Succes!");
            alert.setContentText("Rekord has been succesful deleted");
            alert.show();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            alert.setTitle("Error!");
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.show();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void update(Long id, String postCode, String adress, String place, String voivoship, String county, String comments) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        try {
            transaction = session.beginTransaction();
            Code codeToUpdate = session.get(Code.class, id);
            codeToUpdate.setPostCode(postCode);
            codeToUpdate.setAdress(adress);
            codeToUpdate.setPlace(place);
            codeToUpdate.setVoivoship(voivoship);
            codeToUpdate.setCounty(county);
            codeToUpdate.setComments(comments);
            session.update(codeToUpdate);
            transaction.commit();

            alert.setTitle("Succes!");
            alert.setContentText("Record has benn succesful updated");
            alert.show();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            alert.setTitle("Error!");
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.show();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
