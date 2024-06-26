package org.zerobase.jwitter.domain.stat;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.stat.spi.StatisticsFactory;
import org.hibernate.stat.spi.StatisticsImplementor;

public class TransactionStatisticsFactory implements StatisticsFactory {
    @Override
    public StatisticsImplementor buildStatistics(
            SessionFactoryImplementor sessionFactory) {

        return new TransactionStatistics(sessionFactory);
    }
}
