package repositories;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class ManipulaArquivo {

	public <T, V> List<T> fazBusca(Class<T> tipo, V valor, String query) {
	    EntityManager em = JPAUtil.getEntityManager();
	    try {
	        return new ArrayList<>(
	            em.createQuery(query, tipo)
	              .setParameter("valor", valor)
	              .getResultList());
	    } finally {
	        em.close();
	    }
	}

	public <T, V> List<T> buscar(Class<T> tipo, String atributo, V valor) {
	    String query = "select a from " + tipo.getName()
	        + " a where lower(cast(a." + atributo + " as string)) like lower(:valor) order by a.id";
	    return fazBusca(tipo, valor + "%", query);
	}

	public <T, V> List<T> buscaExata(Class<T> tipo, String atributo, V valor) {
	    String query = "select a from " + tipo.getName()
	        + " a where a." + atributo + " = :valor order by a.id";
	    return fazBusca(tipo, valor, query);
	}
	
	public <T> T buscaUnica(Class<T> tipo, int id) {
		EntityManager em = JPAUtil.getEntityManager();
		
		try {
			return em.find(tipo, id);
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

    public <T> List<T> carregarTudo(Class<T> tipo) {

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