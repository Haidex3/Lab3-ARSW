
package edu.eci.blacklistvalidator.threads;

import java.util.LinkedList;
import java.util.List;

import edu.eci.blacklistvalidator.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

/*
     * Constructor to initialize the thread with its range, IP, and data source facade.
     * @param startIdx Start index of the blacklist range (inclusive)
     * @param endIdx End index of the blacklist range (exclusive)
     * @param ip IP address to check
     * @param skds HostBlacklistsDataSourceFacade to query blacklist servers
     */
public class HostBlackListThread extends Thread {
    public int startIdx;
    public int endIdx;
    private final String ip;
    private final HostBlacklistsDataSourceFacade skds;
    private final List<Integer> localOccurrences;
    private static volatile boolean finished = false;
    private static final int BLACK_LIST_ALARM_COUNT = 5;

    /*
     * Constructor to initialize the thread with its range, IP, and data source
     * facade.
     * 
     * @param startIdx Start index of the blacklist range (inclusive)
     * 
     * @param endIdx End index of the blacklist range (exclusive)
     * 
     * @param ip IP address to check
     * 
     * @param skds HostBlacklistsDataSourceFacade to query blacklist servers
     */
    public HostBlackListThread(int startIdx, int endIdx, String ip, HostBlacklistsDataSourceFacade skds) {
        this.startIdx = startIdx;
        this.endIdx = endIdx;
        this.ip = ip;
        this.skds = skds;
        this.localOccurrences = new LinkedList<>();
    }

    /*
     * Override the run() method from Thread.
     * Iterates through the assigned blacklist range and checks if the IP
     * appears in each server. Records any occurrences in the local list.
     */
    @Override
    public void run() {
        for (int i = startIdx; i < endIdx && !finished; i++) {
            if (skds.isInBlackListServer(i, ip)) {
                localOccurrences.add(i);
                if (localOccurrences.size() >= BLACK_LIST_ALARM_COUNT) {
                    finished = true;
                    return;
                }
            }
        }
    }

    /*
     * Returns the list of blacklist server indices where the IP was found.
     * 
     * @return List<Integer> containing server indices
     */
    public List<Integer> getOccurrences() {
        return localOccurrences;
    }
}
