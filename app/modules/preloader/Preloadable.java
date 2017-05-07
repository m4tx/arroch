package modules.preloader;

import javax.persistence.EntityManager;

public interface Preloadable {
    void run(EntityManager em);
}
