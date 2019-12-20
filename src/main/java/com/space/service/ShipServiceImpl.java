package com.space.service;

//import com.google.common.collect.Lists;
//import com.querydsl.core.BooleanBuilder;
//import com.space.model.QShip;

import com.space.exeptions.BadRequestException;
import com.space.exeptions.ShipNotFoundException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ShipServiceImpl implements ShipService {

    private ShipRepository shipRepository;

    @Autowired
    public void setShipRepository(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    @Override
    public List<Ship> getFilteredShipsList(
            String name,
            String planet,
            ShipType shipType,
            Long after,
            Long before,
            Boolean isUsed,
            Double minSpeed,
            Double maxSpeed,
            Integer minCrewSize,
            Integer maxCrewSize,
            Double minRating,
            Double maxRating
    ) {

        final List<Ship> result = new ArrayList<>();

//        Calendar calendarThisShip = Calendar.getInstance();
//        Integer prodYearBefore = null;
//        Calendar calendarBefore = Calendar.getInstance();
//            if (before != null) {
//                calendarBefore.setTimeInMillis(before);
//                prodYearBefore = calendarBefore.get(Calendar.YEAR);
//            }

//        Calendar calendarAfter = Calendar.getInstance();
//        Integer prodYearAfter = null;
//        if (after != null) {
//            calendarBefore.setTimeInMillis(after);
//            prodYearAfter = calendarAfter.get(Calendar.YEAR);
//        }

        Iterator<Ship> iterator = shipRepository.findAll().iterator();

        while (iterator.hasNext()) {
            Ship ship = iterator.next();
//            calendarThisShip.setTime(ship.getProdDate());
//            int prodYearShip = calendarThisShip.get(Calendar.YEAR);
            if (
                    (name == null || ship.getName().contains(name)) &&
                    (planet == null || ship.getPlanet().contains(planet)) &&
                    (shipType == null || ship.getShipType() == shipType) &&
                    (before == null || ship.getProdDate().before(new Date(before))) &&
//                    (before == null || prodYearShip < prodYearBefore) && // todo: working!!!! but why???
                    (after == null || ship.getProdDate().after(new Date(after))) &&
//                    (after == null || prodYearShip > prodYearAfter) &&
//                    (isUsed == null || ship.getUsed().booleanValue() == isUsed.booleanValue()) &&
                    (isUsed == null || ship.getUsed() == isUsed) &&
                    (minSpeed == null || ship.getSpeed().compareTo(minSpeed) >=0) &&
                    (maxSpeed == null || ship.getSpeed().compareTo(maxSpeed) <= 0) &&
                    (minCrewSize == null || ship.getCrewSize().compareTo(minCrewSize) > 0) &&
                    (maxCrewSize == null || ship.getCrewSize().compareTo(maxCrewSize) < 0) &&
                    (minRating == null || ship.getRating() > minRating) &&
                    (maxRating == null || ship.getRating() < maxRating)
            )
                result.add(ship);
        };

        return result;
    }

    @Override
    public void deleteShip(Long id) {
        shipRepository.deleteById(getShipById(id).getId());
    }

    @Override
    public Ship getShipById(Long id) {
        if (id == 0) {
            throw new BadRequestException("ID must be not null !!!");
        }
        if (shipRepository.existsById(id)) {
            return shipRepository.findById(id).get();
        } else {
            throw new ShipNotFoundException("Ship Not Found!!!");
        }
    }

    @Override
    public Ship createShip(Ship newShip) {
        if (newShip.getName() == null
                || newShip.getPlanet() == null
                || newShip.getShipType() == null
                || newShip.getProdDate() == null
                || newShip.getSpeed() == null
                || newShip.getCrewSize() == null) {
            throw new BadRequestException("One of parameters is null !");
        }

        checkShipData(newShip);

        if (newShip.getUsed() == null) {
            newShip.setUsed(false);
        }

        newShip.setRating(calculateRating(newShip));

        return shipRepository.save(newShip);
    }


    @Override
    public Ship updateShip(Long id, Ship ship) {
        checkShipData(ship);

        Ship shipToUpdate = getShipById(id);

        if (ship.getName() != null) {
            shipToUpdate.setName(ship.getName());
        }

        if (ship.getPlanet() != null) {
            shipToUpdate.setPlanet(ship.getPlanet());
        }

        if (ship.getShipType() != null) {
            shipToUpdate.setShipType(ship.getShipType());
        }

        if (ship.getProdDate() != null) {
            shipToUpdate.setProdDate(ship.getProdDate());
        }

        if (ship.getSpeed() != null) {
            shipToUpdate.setSpeed(ship.getSpeed());
        }

        if (ship.getUsed() != null) {
            shipToUpdate.setUsed(ship.getUsed());
        }

        if (ship.getCrewSize() != null) {
            shipToUpdate.setCrewSize(ship.getCrewSize());
        }

        shipToUpdate.setRating(calculateRating(shipToUpdate));

        return shipRepository.save(shipToUpdate);
    }

    private Double calculateRating(Ship ship) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ship.getProdDate());
        int shipProdYear = calendar.get(Calendar.YEAR);
        BigDecimal raiting = new BigDecimal((80 * ship.getSpeed() * (ship.getUsed() ? 0.5 : 1)) / (3019 - shipProdYear + 1));
        raiting = raiting.setScale(2, RoundingMode.HALF_UP);

        return raiting.doubleValue();
    }

    private void checkShipData(Ship ship) {
        if (ship.getName() != null && (ship.getName().length() < 1 || ship.getName().length() > 50)) {
            throw new BadRequestException("Invalid ship's name !");
        }

        if (ship.getPlanet() != null && (ship.getPlanet().length() < 1 || ship.getPlanet().length() > 50)) {
            throw new BadRequestException("Invalid ship's planet !");
        }

        if (ship.getSpeed() != null && (ship.getSpeed() < 0.01D || ship.getSpeed() > 0.99D)) {
            throw new BadRequestException("Invalid ship's speed !");
        }

        if (ship.getCrewSize() != null && (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999)) {
            throw new BadRequestException("Invalid ship's crew size !");
        }

        if (ship.getProdDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(ship.getProdDate());
            int shipProdYear = calendar.get(Calendar.YEAR);

            if (shipProdYear < 2800 || shipProdYear > 3019) {
                throw new BadRequestException("Invalid ship's prod date !");
            }
        }
    }

    public ShipServiceImpl() {
    }
}
