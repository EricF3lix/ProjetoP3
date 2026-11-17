package repositories;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.QueryTimeoutException;

public class ManipulaArquivo<T> {

	public <T> void atualizar(Class<T> tipo, String atributo, Object valor, int id) {
	    EntityManager em = JPAUtil.getEntityManager();
	    EntityTransaction et = em.getTransaction();

	    try {
	        et.begin();
	        em.createQuery("UPDATE " + tipo.getName() + " t SET t." + atributo + " = :valor WHERE t.id = :id")
	            .setParameter("valor", valor)
	            .setParameter("id", id)
	            .executeUpdate();
	        et.commit();
	    }
	    catch (RuntimeException e) {
	        if (et.isActive()) et.rollback();
	        throw e;
	    }
	    finally {
	        em.close();
	    }
	}

	public <T> void desativar(Class<T> tipo, int id) {
		EntityManager em = JPAUtil.getEntityManager();
	    EntityTransaction et = em.getTransaction();

	    try {
	    	et.begin();
	    	em.createQuery("UPDATE " + tipo.getName() + " t SET t.ativo = :novo WHERE t.id = :id")
	    	.setParameter("novo", false).setParameter("id", id).executeUpdate();
	    	et.commit();

	    }

	    catch (RuntimeException e) {
	        if (et.isActive()) et.rollback();
	        throw e;
	    }

	    finally {
	    	em.close();
	    }

	}

	public <T> void escrever(T tipo) {
	    EntityManager em = JPAUtil.getEntityManager();
	    EntityTransaction et = em.getTransaction();

	    try {
	        et.begin();
	        em.persist(tipo);
	        et.commit();
	    }

	    catch (RuntimeException e) {
	        if (et.isActive()) {
	            et.rollback();
	        }
	        throw e;
	    }

	    finally {
	        em.close();
	    }
	}

    public <T> ArrayList<T> carregarTudo(Class<T> tipo) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
        	List<T> lista = em.createQuery("select a from " + tipo.getName() + " a" + " order by a.id", tipo).getResultList();
        	return new ArrayList<>(lista);

        }

        catch (IllegalArgumentException | IllegalStateException e) {
        // Erro na query
        throw e;
        }

        catch (QueryTimeoutException e) {
        // Timeout na execução da query.
        throw e;
        }

        catch (PersistenceException e) {
        // Erro de persistência
        throw e;
    }
        finally {
        	em.close();
        }
    }



}