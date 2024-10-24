package com.example.transitsync;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BusStopsManager {
    private final List<LatLng> busStopLatLngs = new ArrayList<>();

    // Method to get bus stop locations for the specified destination
    public List<LatLng> getBusStopLocations(String toLocation) {
        busStopLatLngs.clear(); // Clear the list before adding new stops

        // Use a switch statement to add bus stop locations
        switch (toLocation.toLowerCase()) {
            case "trichy":
            case "tiruchirapalli":
                addBusStops(new LatLng[]{
                        new LatLng(10.031664719570538, 78.33813006002595), // Melur
                        new LatLng(10.220211301494112, 78.38177743194308), // Kottampatti
                        new LatLng(10.378700894488235, 78.38810950899274), // Thuvankurichi
                        new LatLng(10.603650499814558, 78.54616073952953)   // Viralimalai

                });
                break;
            case "virudunagar":
                addBusStops(new LatLng[]{
                        new LatLng(9.90076119507132, 78.16571814531451),  // Viraganoor
                        new LatLng(9.887063285825137, 78.1498856128198),   // Anuppanadi
                        new LatLng(9.831035884460702, 78.1014054730237),   // Transport Nagar
                        new LatLng(9.835682362437767, 78.03432793438847),   // Kappalur
                        new LatLng(9.821622011441576, 77.98817285467143),   // Tirumangalam
                        new LatLng(9.710396741036678, 77.97281993278128)    // Kalligudi
                });
                break;
            case "coimbatore":
            case "kovai":
                addBusStops(new LatLng[]{
                        new LatLng(9.964748020085343, 78.06678256687798),  // Paravai
                        new LatLng(9.979374514127093, 78.03270173627483),  // Samayanallur
                        new LatLng(10.087063460532013, 77.96044666514466),  // Vadipatti
                        new LatLng(10.23437166847572, 77.89898239156584),   // Kamalapuram
                        new LatLng(10.282098249228465, 77.8720418593486),   // Sempatti
                        new LatLng(10.49035887214724, 77.75798744651391),   // Oddanchatram
                        new LatLng(10.735874784446818, 77.52539402190622),  // Dharapuram
                        new LatLng(10.995784092791842, 77.28653504812287),  // Palladam
                        new LatLng(11.025197059672134, 77.1242809468234)    // Sulur
                });
                break;
            case "sivakasi":
                addBusStops(new LatLng[]{
                        new LatLng(9.90076119507132, 78.16571814531451),   // Viraganoor
                        new LatLng(9.887063285825137, 78.1498856128198),    // Anuppanadi
                        new LatLng(9.831035884460702, 78.1014054730237),    // Transport Nagar
                        new LatLng(9.835682362437767, 78.03432793438847),    // Kappalur
                        new LatLng(9.821622011441576, 77.98817285467143),    // Tirumangalam
                        new LatLng(9.710396741036678, 77.97281993278128),    // Kalligudi
                        new LatLng(9.5680222655915, 77.96093587665884),      // Virudhunagar
                        new LatLng(9.566308044437587, 77.86197152666158),    // Amathur
                        new LatLng(9.506332706972314, 77.83953267908497)     // Vadamalapuram
                });
                break;
            case "paramakudi":
                addBusStops(new LatLng[]{
                        new LatLng(9.90076119507132, 78.16571814531451),   // Viraganoor
                        new LatLng(9.826748978039104, 78.25556030472625),   // Thiruppuvanam
                        new LatLng(9.696051752332517, 78.45656999197135),   // Manamadurai
                        new LatLng(9.587626539096998, 78.45671603267999)    // Parthibanur (Ramnad District)
                });
                break;
            case "theni":
                addBusStops(new LatLng[]{
                        new LatLng(9.933763502951392, 78.04796367801438),   // Nagamalai Pudukkottai
                        new LatLng(9.942592923622342, 77.97190073402918),   // Checkanurani
                        new LatLng(9.964881555536346, 77.78863893040254),   // Usilampatti
                        new LatLng(10.000452078747106, 77.619004558733249)  // Andipatti
                });
                break;
            case "ramnad":
                addBusStops(new LatLng[]{
                        new LatLng(9.90076119507132, 78.16571814531451),   // Viraganoor
                        new LatLng(9.826748978039104, 78.25556030472625),   // Thiruppuvanam
                        new LatLng(9.696051752332517, 78.45656999197135),    // Manamadurai
                        new LatLng(9.587626539096998, 78.45671603267999),   // Parthibanur (Ramnad District)
                        new LatLng(9.550676744734862, 78.58448222061764)    // Paramakudi
                });
                break;
            case "kodaikanal":
                addBusStops(new LatLng[]{
                        new LatLng(9.964748020085343, 78.06678256687798),  // Paravai
                        new LatLng(9.979374514127093, 78.03270173627483),  // Samayanallur
                        new LatLng(10.087063460532013, 77.96044666514466), // Vadipatti
                        new LatLng(10.165031260452862, 77.85358346242785), // Nilakottai
                        new LatLng(10.163465638366741, 77.75693412105699)  // Batlagundu
                });
                break;
            case "rajapalayam":
                addBusStops(new LatLng[]{
                        new LatLng(9.90076119507132, 78.16571814531451),   // Viraganoor
                        new LatLng(9.887063285825137, 78.1498856128198),    // Anuppanadi
                        new LatLng(9.831035884460702, 78.1014054730237),    // Transport Nagar
                        new LatLng(9.835682362437767, 78.03432793438847),    // Kappalur
                        new LatLng(9.821622011441576, 77.98817285467143),    // Tirumangalam
                        new LatLng(9.721239633559946, 77.85552881558337),    // T. Kallupatti (Madurai District)
                        new LatLng(9.513383342178843, 77.63738133656273)     // Srivilliputhur
                });
                break;
            case "sattur":
                addBusStops(new LatLng[]{
                        new LatLng(9.90076119507132, 78.16571814531451),   // Viraganoor
                        new LatLng(9.887063285825137, 78.1498856128198),    // Anuppanadi
                        new LatLng(9.831035884460702, 78.1014054730237),    // Transport Nagar
                        new LatLng(9.835682362437767, 78.03432793438847),    // Kappalur
                        new LatLng(9.821622011441576, 77.98817285467143),    // Tirumangalam
                        new LatLng(9.710396741036678, 77.97281993278128),    // Kalligudi
                        new LatLng(9.5680222655915, 77.96093587665884)       // Virudhunagar
                });
                break;
            case "kovilpatti":
                addBusStops(new LatLng[]{
                        new LatLng(9.90076119507132, 78.16571814531451),   // Viraganoor
                        new LatLng(9.887063285825137, 78.1498856128198),    // Anuppanadi
                        new LatLng(9.831035884460702, 78.1014054730237),    // Transport Nagar
                        new LatLng(9.835682362437767, 78.03432793438847),    // Kappalur
                        new LatLng(9.821622011441576, 77.98817285467143),    // Tirumangalam
                        new LatLng(9.710396741036678, 77.97281993278128),    // Kalligudi
                        new LatLng(9.5680222655915, 77.96093587665884),      // Virudhunagar
                        new LatLng(9.357158613205344, 77.91566188340128)      // Sattur
                });
                break;
            case "thirunelveli":
            case "nellai":
                addBusStops(new LatLng[]{
                        new LatLng(9.90076119507132, 78.16571814531451),   // Viraganoor
                        new LatLng(9.887063285825137, 78.1498856128198),    // Anuppanadi
                        new LatLng(9.831035884460702, 78.1014054730237),    // Transport Nagar
                        new LatLng(9.835682362437767, 78.03432793438847),    // Kappalur
                        new LatLng(9.821622011441576, 77.98817285467143),    // Tirumangalam
                        new LatLng(9.710396741036678, 77.97281993278128),    // Kalligudi
                        new LatLng(9.5680222655915, 77.96093587665884),      // Virudhunagar
                        new LatLng(9.357158613205344, 77.91566188340128),      // Sattur
                        new LatLng(9.172864952371825, 77.87160849320773)      // Kovilpatti
                });
                break;
            case "tiruppur":
                addBusStops(new LatLng[]{
                        new LatLng(9.964748020085343, 78.06678256687798),  // Paravai
                        new LatLng(9.979374514127093, 78.03270173627483),  // Samayanallur
                        new LatLng(10.087063460532013, 77.96044666514466), // Vadipatti
                        new LatLng(10.23437166847572, 77.89898239156584),   // Kamalapuram
                        new LatLng(10.282098249228465, 77.8720418593486),   // Sempatti
                        new LatLng(10.49035887214724, 77.75798744651391),   // Oddanchatram
                        new LatLng(10.735874784446818, 77.52539402190622)    // Dharapuram
                });
                break;
            case "nagercoil":
                addBusStops(new LatLng[]{
                        new LatLng(9.90076119507132, 78.16571814531451),   // Viraganoor
                        new LatLng(9.887063285825137, 78.1498856128198),    // Anuppanadi
                        new LatLng(9.831035884460702, 78.1014054730237),    // Transport Nagar
                        new LatLng(9.835682362437767, 78.03432793438847),    // Kappalur
                        new LatLng(9.821622011441576, 77.98817285467143),    // Tirumangalam
                        new LatLng(9.710396741036678, 77.97281993278128),    // Kalligudi
                        new LatLng(9.5680222655915, 77.96093587665884),      // Virudhunagar
                        new LatLng(9.357158613205344, 77.91566188340128),      // Sattur
                        new LatLng(9.172864952371825, 77.87160849320773),      // Kovilpatti
                        new LatLng(8.713979186801675, 77.77170293841294)       // Thirunelveli
                });
                break;
            case "srivilliputhur":
                addBusStops(new LatLng[]{
                        new LatLng(9.90076119507132, 78.16571814531451),   // Viraganoor
                        new LatLng(9.887063285825137, 78.1498856128198),    // Anuppanadi
                        new LatLng(9.831035884460702, 78.1014054730237),    // Transport Nagar
                        new LatLng(9.835682362437767, 78.03432793438847),   // Kappalur
                        new LatLng(9.821622011441576, 77.98817285467143),   // Tirumangalam
                        new LatLng(9.721239633559946, 77.85552881558337)  // T. Kallupatti (Madurai District)
                });
                break;

            case "batlagundu":
                addBusStops(new LatLng[]{
                        new LatLng(9.964748020085343, 78.06678256687798),  // Paravai
                        new LatLng(9.979374514127093, 78.03270173627483),  // Samayanallur
                        new LatLng(10.087063460532013, 77.96044666514466),  // Vadipatti
                        new LatLng(10.165031260452862, 77.85358346242785)   // Nilakottai
                });
                break;
            case "thanjavur":
            case "tanjore":
                addBusStops(new LatLng[]{
                        new LatLng(10.031664719570538, 78.33813006002595),  // Melur
                        new LatLng(10.107955051130826, 78.59805089773428),   // Thirupathur (Sivaganga District)
                        new LatLng(10.246892306718571, 78.749689507008),    // Thirumayam
                        new LatLng(10.573580057651116, 79.01347002966655)   // Gandharvakkottai
                });
                break;
            case "chennai":
            case "madras":
                addBusStops(new LatLng[]{
                        new LatLng(10.031664719570538, 78.33813006002595),  // Melur
                        new LatLng(10.220211301494112, 78.38177743194308),  // Kottampatti
                        new LatLng(10.378700894488235, 78.38810950899274),  // Thuvankurichi
                        new LatLng(10.603650499814558, 78.54616073952953),  // Viralimalai
                        new LatLng(10.790230343150702, 78.70857268026077),   // Trichy
                        new LatLng(11.234820548474966, 78.88332449361666),   // Perambalur
                        new LatLng(11.676804854874037, 79.2874148166318),    // Ulundurpet
                        new LatLng(11.93870680154976, 79.48692083285518),    // Villupuram
                        new LatLng(12.226304628893985, 79.65075010893437),   // Tindivanam
                        new LatLng(12.037193784042081, 79.54682919815248),   // Vikravandi
                        new LatLng(12.682133361388374, 79.98858662402355),   // Chengalpattu
                        new LatLng(12.96751767089572, 80.14915804345344),    // Pallavaram
                        new LatLng(12.925076103855268, 80.10126183222538)     // Tambaram
                });
                break;
            case "kanyakumari":
                addBusStops(new LatLng[]{
                        new LatLng(9.90076119507132, 78.16571814531451),     // Viraganoor
                        new LatLng(9.887063285825137, 78.1498856128198),      // Anuppanadi
                        new LatLng(9.831035884460702, 78.1014054730237),      // Transport Nagar
                        new LatLng(9.835682362437767, 78.03432793438847),      // Kappalur
                        new LatLng(9.821622011441576, 77.98817285467143),      // Tirumangalam
                        new LatLng(9.710396741036678, 77.97281993278128),      // Kalligudi
                        new LatLng(9.5680222655915, 77.96093587665884),       // Virudhunagar
                        new LatLng(9.357158613205344, 77.91566188340128),     // Sattur
                        new LatLng(9.172864952371825, 77.87160849320773),      // Kovilpatti
                        new LatLng(8.713979186801675, 77.77170293841294)       // Thirunelveli
                });
                break;
            case "rameswaram":
                addBusStops(new LatLng[]{
                        new LatLng(9.90076119507132, 78.16571814531451),      // Viraganoor
                        new LatLng(9.826748978039104, 78.25556030472625),      // Thiruppuvanam
                        new LatLng(9.696051752332517, 78.45656999197135),      // Manamadurai
                        new LatLng(9.587626539096998, 78.45671603267999),      // Parthibanur (Ramnad District)
                        new LatLng(9.550676744734862, 78.58448222061764),      // Paramakudi
                        new LatLng(9.363966940358457, 78.83814900811409),      // Ramanathapuram
                        new LatLng(9.27738003910911, 79.12902838403694)        // Mandapam
                });
                break;
            case "thoothukudi":
            case "tuticorin":
                addBusStops(new LatLng[]{
                        new LatLng(9.90076119507132, 78.16571814531451),      // Viraganoor
                        new LatLng(9.887063285825137, 78.1498856128198),       // Anuppanadi
                        new LatLng(9.831035884460702, 78.1014054730237),       // Transport Nagar
                        new LatLng(9.812291778204711, 78.09879105127754),      // Valayangulam (Madurai District)
                        new LatLng(9.395306084092253, 78.1083498077489),       // Pandalkudi
                        new LatLng(9.674641612130662, 78.1029713604619),       // Kariapatti
                        new LatLng(9.514050803610298, 78.10072539179968),      // Aruppukottai
                        new LatLng(9.147675066957605, 77.98951920702456)       // Ettayapuram
                });
                break;
            case "pollachi":
                addBusStops(new LatLng[]{
                        new LatLng(9.964748020085343, 78.06678256687798),      // Paravai
                        new LatLng(9.979374514127093, 78.03270173627483),      // Samayanallur
                        new LatLng(10.087063460532013, 77.96044666514466),      // Vadipatti
                        new LatLng(10.23437166847572, 77.89898239156584),       // Kamalapuram
                        new LatLng(10.282098249228465, 77.8720418593486),      // Sempatti
                        new LatLng(10.49035887214724, 77.75798744651391),      // Oddanchatram
                        new LatLng(10.449237836314458, 77.51599374553061),     // Palani
                        new LatLng(10.582200898109013, 77.2568722018005)       // Udumalpet
                });
                break;
            case "palani":
                addBusStops(new LatLng[]{
                        new LatLng(9.964748020085343, 78.06678256687798),      // Paravai
                        new LatLng(9.979374514127093, 78.03270173627483),      // Samayanallur
                        new LatLng(10.087063460532013, 77.96044666514466),      // Vadipatti
                        new LatLng(10.23437166847572, 77.89898239156584),       // Kamalapuram
                        new LatLng(10.282098249228465, 77.8720418593486),      // Sempatti
                        new LatLng(10.49035887214724, 77.75798744651391),      // Oddanchatram
                        new LatLng(10.449237836314458, 77.51599374553061),     // Palani
                        new LatLng(10.582200898109013, 77.2568722018005)       // Udumalpet
                });
                break;
            case "ooty":
                addBusStops(new LatLng[]{
                        new LatLng(9.964748020085343, 78.06678256687798),  // Paravai
                        new LatLng(9.979374514127093, 78.03270173627483),  // Samayanallur
                        new LatLng(10.087063460532013, 77.96044666514466),  // Vadipatti
                        new LatLng(10.27920895427043, 77.92676658530152),   // Chinnalapatti
                        new LatLng(10.362653599882954, 77.97068098728576),   // Dindigul
                        new LatLng(10.49035887214724, 77.75798744651391),   // Oddanchatram
                        new LatLng(10.735874784446818, 77.52539402190622),  // Dharapuram
                        new LatLng(10.995784092791842, 77.28653504812287),  // Palladam
                        new LatLng(11.018012669184357, 77.1843479471724),   // Karanampettai
                        new LatLng(11.104992051201972, 77.17920159557023),  // Karumathampatti
                        new LatLng(11.2319472447323, 77.10678046872361),    // Annur
                        new LatLng(11.302036615633272, 76.9373693375828),   // Mettupalayam
                        new LatLng(11.343387779123868, 76.79431110543081),  // Coonoor
                        new LatLng(11.38339003343969, 76.72904228827798)    // Ketti
                });
                break;
            default:
                // No bus stops for unknown locations
                break;
        }

        return new ArrayList<>(busStopLatLngs);
    }

    // Helper method to add bus stop locations to the list
    private void addBusStops(LatLng[] latLngs) {
        busStopLatLngs.addAll(Arrays.asList(latLngs));
    }
}
