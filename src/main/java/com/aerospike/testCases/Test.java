package com.aerospike.testCases;

import com.aerospike.client.Key;
import com.aerospike.data.dataGenator.DataProvider;
import com.aerospike.utilities.aerospike.AerospikeConnection;
import com.aerospike.utilities.logger.Logable;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public abstract class Test<T> implements Logable {
        protected final AerospikeConnection connection;
        protected final int numberOfThreads;
        private final DataProvider<T> dataProvider;
        private final ThreadPoolExecutor executor;
        private final AtomicInteger tpsCounter = new AtomicInteger();

        protected Test(AerospikeConnection connection, int numberOfThreads, DataProvider<T> dataProvider) {
            this.connection = connection;
            this.numberOfThreads = numberOfThreads;
            this.dataProvider = dataProvider;
            executor = new ThreadPoolExecutor(numberOfThreads, numberOfThreads, 100, TimeUnit.MINUTES, new ArrayBlockingQueue<>(500000));
        }

        public void run() {
            for (int i = 0; i < numberOfThreads; i++)
                executor.execute(this::loop);
        }
        private void loop(){
            Stream.generate(dataProvider::next).forEach(this::action);
        }

        private void action(T t){
            execute(t);
            tpsCounter.getAndIncrement();
        }

        protected abstract void execute(T t);

        public int getTPS() {
            return tpsCounter.getAndSet(0);
        }

        protected Key getKey(int key){
            return new Key(connection.getNamespace(), connection.getSetName(), key);
        }
}
