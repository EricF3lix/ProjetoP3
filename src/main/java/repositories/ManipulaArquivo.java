package repositories;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;

public class ManipulaArquivo {
	
	public <T> T buscaUnica(Class<T> tipo, int id) {
		EntityManager em = JPAUtil.getEntityManager();
		
		try {
			return em.find(tipo, id);
		}
		
		finally {
			em.close();
		}
	}

	public <T, V> ArrayList<T> buscar(Class<T> tipo, String atributo, V valor) {
	    EntityManager em = JPAUtil.getEntityManager();

	    try {
	        List<T> lista = em.createQuery(
	                "select a from " + tipo.getName()
	                + " a where lower(cast(a." + atributo + " as String)) like lower(:valor) order by a.id", tipo)
	            .setParameter("valor", valor + "%")
	            .getResultList();
	        return new ArrayList<>(lista);
	    }
	    finally {
	        em.close();
	    }
	}

	public <T, V> boolean atualizar(Class<T> tipo, String atributo, V valor, int id) {
	    EntityManager em = JPAUtil.getEntityManager();
	    EntityTransaction et = em.getTransaction();
	    boolean sucesso;

	    try {
	        et.begin();
	        em.createQuery("UPDATE " + tipo.getName() + " t SET t." + atributo + " = :valor WHERE t.id = :id")
	            .setParameter("valor", valor)
	            .setParameter("id", id)
	            .executeUpdate();
	        et.commit();
	        sucesso = true;
	    }
	    catch (IllegalArgumentException e) {
	        if (et.isActive()) et.rollback();
	        sucesso = false;
	        throw e;
	    }
	    catch (PersistenceException e) {
	        if (et.isActive()) et.rollback();
	        sucesso = false;
	        throw e;
	    }
	    catch (RuntimeException e) {
	        if (et.isActive()) et.rollback();
	        sucesso = false;
	        throw e;
	    }
	    finally {
	        em.close();
	    }

	    return sucesso;
	}
	
	public <T> boolean desativar(Class<T> tipo, int id) {
	    EntityManager em = JPAUtil.getEntityManager();
	    EntityTransaction et = em.getTransaction();
	    boolean sucesso;
	    
	    try {
	        et.begin();
	        em.createQuery("UPDATE " + tipo.getName() + " t SET t.ativo = :novo WHERE t.id = :id")
	            .setParameter("novo", false)
	            .setParameter("id", id)
	            .executeUpdate();
	        et.commit();
	        sucesso = true;
	    }
	    catch (IllegalArgumentException e) {
	        if (et.isActive()) et.rollback();
	        sucesso = false;
	        throw e;
	    }
	    catch (PersistenceException e) {
	        if (et.isActive()) et.rollback();
	        sucesso = false;
	        throw e;
	    }
	    catch (RuntimeException e) {
	        if (et.isActive()) et.rollback();
	        sucesso = false;
	        throw e;
	    }
	    finally {
	        em.close();
	    }
	    
	    return sucesso;
	}
	
	public <T> boolean escrever(T tipo) {
	    EntityManager em = JPAUtil.getEntityManager();
	    EntityTransaction et = em.getTransaction();
	    boolean sucesso;

	    try {
	        et.begin();
	        em.persist(tipo);
	        et.commit();
	        sucesso = true;
	    }
	    catch (IllegalArgumentException e) {
	        if (et.isActive()) et.rollback();
	        sucesso = false;
	        throw e;
	    }
	    catch (PersistenceException e) {
	        if (et.isActive()) et.rollback();
	        sucesso = false;
	        throw e;
	    }
	    catch (RuntimeException e) {
	        if (et.isActive()) et.rollback();
	        sucesso = false;
	        throw e;
	    }
	    finally {
	        em.close();
	    }

	    return sucesso;
	}

    public <T> ArrayList<T> carregarTudo(Class<T> tipo) {

        EntityManager em = JPAUtil.getEntityManager();
        
        try {
        	List<T> lista = em.createQuery("select a from " + tipo.getName() + " a" + " order by a.id", tipo).getResultList();
        	return new ArrayList<>(lista);
        	
        }

        finally {
        	em.close();
        }
    }
    
    

}